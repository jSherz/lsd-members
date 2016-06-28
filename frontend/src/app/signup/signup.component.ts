import { Component }                  from '@angular/core';
import { AppComponent }               from '../app.component';

@Component({
  moduleId: module.id,
  selector: 'app-root',
  templateUrl: 'signup.component.html'
})

export class SignupComponent {
  constructor(private appComponent: AppComponent) {
    appComponent.setTitle('Sign-up')
  }
}
