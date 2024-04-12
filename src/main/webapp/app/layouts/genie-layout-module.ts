import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';

import { ButtonModule } from 'primeng/button';
import { StepsModule } from 'primeng/steps';
import { PanelModule } from 'primeng/panel';
import { ToastModule } from 'primeng/toast';
import { CheckboxModule} from 'primeng/checkbox';
import { InputTextModule} from 'primeng/inputtext';
import { ScrollPanelModule} from 'primeng/scrollpanel';
import { TabViewModule} from 'primeng/tabview';
import { TooltipModule} from 'primeng/tooltip';
import { SidebarModule} from 'primeng/sidebar';
import { MenuModule} from 'primeng/menu';
import { PanelMenuModule} from 'primeng/panelmenu';
import { MegaMenuModule } from 'primeng/megamenu';
import { StyleClassModule } from 'primeng/styleclass';
import { GenieSharedModule} from '../shared/shared.module';
import { GenieMenuComponent} from './authenticated/genie-menu.component';
import { GenieFooterComponent} from './shared/genie-footer.component';
import { GenieTopbarComponent } from './shared/genie-topbar.component';
import { GenieLayoutService } from './genie-layout-service.service';
import { GenieDefaultLayoutComponent } from './anonymous/genie-default-layout.component';
import { GenieMenuProfileComponent } from './authenticated/genie-menu-profile.component';
import { GenieBreadcrumbComponent } from './authenticated/genie-breadcrumb.component';
import { GenieMenuitemComponent } from './authenticated/genie-menuitem.component';
import { GenieAuthenticatedLayoutComponent } from './extended/genie-authenticated-layout.component';
import { GenieSidebarComponent } from './authenticated/genie-sidebar.component';
import { RadioButtonModule } from 'primeng/radiobutton';
import { InputSwitchModule } from 'primeng/inputswitch';
import { GenieConfigComponent } from './config/genie-config.component';
import { RippleModule } from 'primeng/ripple';
import { BadgeModule } from 'primeng/badge';

@NgModule({
  imports: [
    ButtonModule,
    CheckboxModule,
    GenieSharedModule,
    InputTextModule,
    StepsModule,
    PanelModule,
    ScrollPanelModule,
    TabViewModule,
    TooltipModule,
    ToastModule,
    SidebarModule,
    MenuModule,
    PanelMenuModule,
    MegaMenuModule,
    StyleClassModule,
    RadioButtonModule,
    InputSwitchModule,
    RippleModule,
    BadgeModule
  ],
  declarations: [
    GenieAuthenticatedLayoutComponent,
    GenieMenuitemComponent,
    GenieBreadcrumbComponent,
    GenieConfigComponent,
    GenieDefaultLayoutComponent,
    GenieMenuProfileComponent,
    GenieFooterComponent,
    GenieMenuComponent,
    GenieTopbarComponent,
    GenieSidebarComponent
  ],
  exports: [
    GenieAuthenticatedLayoutComponent,
    GenieMenuitemComponent,
    GenieBreadcrumbComponent,
    GenieConfigComponent,
    GenieDefaultLayoutComponent,
    GenieMenuProfileComponent,
    GenieFooterComponent,
    GenieMenuComponent,
    GenieTopbarComponent,
    GenieSidebarComponent
  ],
  providers: [GenieLayoutService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GenieLayoutModule {
}
