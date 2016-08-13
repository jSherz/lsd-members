import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {
  FormControl,
  FormBuilder,
  FormGroup,
  Validators,
  REACTIVE_FORM_DIRECTIVES
} from '@angular/forms';
import { CustomValidators } from '../utils';

@Component({
  templateUrl: 'signup-alt.component.html',
  directives: [REACTIVE_FORM_DIRECTIVES]
})
export class SignupAltComponent {

  signupForm: FormGroup;

  ctrlName: FormControl;
  ctrlEmail: FormControl;

  constructor(private builder: FormBuilder, private router: Router) {
    this.ctrlName = new FormControl('', Validators.required);
    this.ctrlEmail = new FormControl('', Validators.compose([Validators.required, CustomValidators.email]));

    this.signupForm = builder.group({
      name: this.ctrlName,
      email: this.ctrlEmail
    });
  }

  signup() {
    this.router.navigate(['sign-up', 'thank-you']);
  }

}
