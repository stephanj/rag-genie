import {Component, OnInit, ElementRef, ViewChild, OnDestroy} from '@angular/core';
import { VectorVisualizationService } from './vector-visualization.service';
import * as THREE from 'three';
import {OrbitControls} from 'three/examples/jsm/controls/OrbitControls';
import {ContentListService} from '../../../shared/content-list/content-list.service';
import { IContent } from '../../../shared/model/content.model';
import {InputTextModule} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {ButtonModule} from 'primeng/button';
import {SelectItem} from 'primeng/api';
import {ListboxModule} from 'primeng/listbox';
import {Subscription} from 'rxjs';
import {DropdownModule} from 'primeng/dropdown';
import {DialogModule} from 'primeng/dialog';
import {NgStyle} from '@angular/common';
import {ColorRepresentation} from 'three/src/math/Color';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'genie-vector-visualization',
  templateUrl: './vector-visualization.component.html',
  imports: [
    InputTextModule,
    FormsModule,
    ButtonModule,
    ListboxModule,
    DropdownModule,
    DialogModule,
    NgStyle
  ],
  standalone: true
})
export class VectorVisualizationComponent implements OnInit, OnDestroy {

  @ViewChild('container') containerRef!: ElementRef;

  private subscriptions = new Subscription();

  private camera =
    new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 1, 10_000);

  private renderer!: THREE.WebGLRenderer;
  private scene = new THREE.Scene();

  private colorIndex = 0;

  content: IContent[] = [];
  contentSelection: SelectItem[] = [];
  dimension: string | null = null;

  constructor(protected vectorVisualizationService: VectorVisualizationService,
              protected contentListService: ContentListService,
              protected activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.dimension = this.activatedRoute.snapshot.paramMap.get('dimension');
    this.loadContent();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();

    if (this.renderer) {
      this.renderer.dispose();
      this.renderer.forceContextLoss();
    }

    if (this.scene) {
      this.scene.clear();
    }

    if (this.camera) {
      this.camera.remove();
    }
  }

  private loadContent() {
    this.subscriptions.add(
      this.contentListService.getContent().subscribe({
        next: (res) => {
          this.content = res.body || [];
          // Now that content is loaded, draw the points
          this.initThreeJS();
          this.drawPoints(); // Make sure this is called here
        },
        error: (err) => {
          console.error('Failed to load content', err);
        }
      })
    );
  }

  private drawPoints() {
    this.content.forEach((c) => {
      if (c.id && this.dimension) {
        this.subscriptions.add(
          this.vectorVisualizationService.getEmbeddings(this.dimension, c.id).subscribe({
            next: (embeddings) => {

              if (embeddings.body) {
                const { numericalData} = this.processVectorData(embeddings.body);

                const color = this.getDistinctColors();
                this.contentSelection.push({label: c.name, value: color});
                this.addPoints(numericalData, color);
              }
            },
            error: (err) => {
              console.error('Failed to load embeddings for content', err);
            }
          })
        );
      }
    });
    this.animate();
  }

  private initThreeJS(): void {
    this.renderer = new THREE.WebGLRenderer();
    this.renderer.setSize(window.innerWidth, window.innerHeight);
    // 0x121212 is a dark grey color
    this.renderer.setClearColor(0x121212, 1.0);

    this.containerRef.nativeElement.appendChild(this.renderer.domElement);
  }

  private addPoints(typedArray: Float32Array, color: ColorRepresentation): void {
    const geometry = new THREE.BufferGeometry();
    geometry.setAttribute('position', new THREE.BufferAttribute(typedArray, 3));
    const material = new THREE.PointsMaterial({ color: color, size: 0.07 });
    const points = new THREE.Points(geometry, material);
    points.position.x -= 2; // 'amount' is how far you want to move the points to the left
    this.scene.add(points);
  }

  processVectorData(data: number[][]): {
    numericalData: Float32Array;
    labelValues: string[];
  } {
    const numericalData: number[] = [];
    const labels: string[] = [];

    // Find the minimum and maximum values for scaling
    const xValues = data.map(([x]) => x);
    const yValues = data.map(([, y]) => y);
    const minX = Math.min(...xValues);
    const maxX = Math.max(...xValues);
    const minY = Math.min(...yValues);
    const maxY = Math.max(...yValues);

    data.forEach(([x, y]) => {
      // Scale and offset the vector data to NDC space
      const scaledX = ((x - minX) / (maxX - minX)) * 2 - 1;
      const scaledY = ((y - minY) / (maxY - minY)) * 2 - 1;

      numericalData.push(scaledX, scaledY);
    });

    const typedNumericalData = new Float32Array(numericalData);

    return { numericalData: typedNumericalData, labelValues: labels };
  }

  private animate(): void {
    this.camera.position.z = 5;
    const controls = new OrbitControls(this.camera, this.renderer.domElement);

    controls.addEventListener('change', () => this.renderer.render(this.scene, this.camera));

    const animate = () => {
      requestAnimationFrame(animate);

      controls.update();

      this.renderer.render(this.scene, this.camera);
    };

    animate();
  }

  getDistinctColors(): string {
    const myColors = [
      '#ff0000', // Red
      '#00ff00', // Green
      '#0000ff', // Blue
      '#ffff00', // Yellow
      '#00ffff', // Cyan
      '#ff00ff', // Magenta
      '#ffa500', // Orange
      '#32cd32', // Lime Green
      '#800080', // Purple
      '#ff69b4', // Hot Pink
    ];

    // Use the current index to get the color
    const color = myColors[this.colorIndex];

    // Increment the index for next use and reset if necessary
    this.colorIndex = (this.colorIndex + 1) % myColors.length;

    return color;
  }
}
