import { Component } from '@angular/core';
import { NgForm }    from '@angular/forms';
import { SignupAlt } from './signup-alt.model';

@Component({
  moduleId: module.id,
  selector: 'app-root',
  templateUrl: 'signup-alt.component.html'
})
export class SignupAltComponent {

  private emailRegex = /.+\@.+\..+/

  user: SignupAlt = new SignupAlt(undefined, undefined);

  signup() {
  }

}
