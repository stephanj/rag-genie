import {Component, OnInit, ElementRef, ViewChild, OnDestroy, Renderer2} from '@angular/core';
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
  private raycaster!: THREE.Raycaster;
  private mouse!: THREE.Vector2;
  private label!: HTMLElement;
  private spheres: { sphere: THREE.Mesh, label: string }[] = [];

  private scene = new THREE.Scene();

  private colorIndex = 0;

  content: IContent[] = [];
  contentSelection: SelectItem[] = [];
  dimension: string | null = null;

  constructor(protected vectorVisualizationService: VectorVisualizationService,
              protected contentListService: ContentListService,
              protected activatedRoute: ActivatedRoute,
              protected renderer2: Renderer2) {
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
    this.raycaster = new THREE.Raycaster();
    this.mouse = new THREE.Vector2();

    this.label = this.renderer2.createElement('div');
    this.label.style.position = 'absolute';
    this.label.style.backgroundColor = 'rgba(255, 255, 255, 0.8)';
    this.label.style.padding = '4px';
    this.label.style.display = 'none';
    document.body.appendChild(this.label);

    this.containerRef.nativeElement.appendChild(this.renderer.domElement);
    this.renderer.domElement.addEventListener('mousemove', this.onMouseMove.bind(this));
  }

  private addPoints(typedArray: Float32Array, color: ColorRepresentation): void {
    const geometry = new THREE.SphereGeometry(0.1);
    const material = new THREE.MeshBasicMaterial({ color: color});
    const sphere = new THREE.Mesh(geometry, material);
    sphere.position.set(typedArray[0], typedArray[1], 0)
    this.spheres.push( {sphere: sphere, label: "dummy label"});
    this.scene.add(sphere);
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

  onMouseMove(event: MouseEvent) {
    event.preventDefault();

    // Calculate mouse position in normalized coordinates
    this.mouse.x = (event.clientX / this.renderer.domElement.clientWidth) * 2 - 1;
    this.mouse.y = -(event.clientY / this.renderer.domElement.clientHeight) * 2 + 1;
    
    // Update the raycaster with the camera and mouse position
    this.raycaster.setFromCamera(this.mouse, this.camera);

    // Calculate objects intersecting the picking ray
    const intersects = this.raycaster.intersectObjects(this.spheres.map(({ sphere }) => sphere));

    if (intersects.length > 0) {
      const intersect = intersects[0];
      const sphereData = this.spheres.find(({ sphere }) => sphere === intersect.object);

      if(sphereData) {
        const { sphere, label } = sphereData
        this.label.innerHTML = `Label: ${label}, x: ${sphere.position.x.toFixed(2)}, y: ${sphere.position.y.toFixed(2)}`;
        this.label.style.left = `${event.clientX + 10}px`;
        this.label.style.top = `${event.clientY + 10}px`;
        this.label.style.display = 'block';
      }
    } else {
      this.label.style.display = 'none';
    }
  }
}
