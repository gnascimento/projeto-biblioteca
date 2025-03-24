import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {StyleClassModule} from 'primeng/styleclass';
import {Toast} from 'primeng/toast';
import {MenuItem} from 'primeng/api';
import {Menubar} from 'primeng/menubar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, StyleClassModule, Toast, Menubar],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: []
})
export class AppComponent {
  title = 'biblioteca-fe';

  items: MenuItem[] = [];

  ngOnInit() {
    this.items = [
      { label: 'Home', icon: 'pi pi-fw pi-home', routerLink: ['/'] },
      { label: 'Autores', icon: 'pi pi-fw pi-user', routerLink: ['/autores'] },
      { label: 'Assuntos', icon: 'pi pi-fw pi-list', routerLink: ['/assuntos'] },
      { label: 'Livros', icon: 'pi pi-fw pi-book', routerLink: ['/livros'] }
    ];
  }

}
