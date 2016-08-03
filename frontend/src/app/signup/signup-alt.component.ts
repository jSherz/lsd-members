import { Component } from '@angular/core';
import {
  FormControl,
  FormBuilder,
  FormGroup,
  Validators,
  FORM_DIRECTIVES,
  REACTIVE_FORM_DIRECTIVES
} from '@angular/forms';
import { CustomValidators } from '../utils';

@Component({
  moduleId: module.id,
  templateUrl: 'signup-alt.component.html',
  directives: [REACTIVE_FORM_DIRECTIVES]
})
export class SignupAltComponent {

  signupForm: FormGroup;

  ctrlName: FormControl;
  ctrlEmail: FormControl;

  constructor(private builder: FormBuilder) {
    this.ctrlName = new FormControl('', Validators.required);
    this.ctrlEmail = new FormControl('', Validators.compose([Validators.required, CustomValidators.email]));

    this.signupForm = builder.group({
      name: this.ctrlName,
      email: this.ctrlEmail
    });
  }

  signup() {
  }

}
