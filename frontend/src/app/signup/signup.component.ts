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
  templateUrl: 'signup.component.html',
  directives: [REACTIVE_FORM_DIRECTIVES]
})
export class SignupComponent {

  signupForm: FormGroup;

  ctrlName: FormControl;
  ctrlPhoneNumber: FormControl;

  constructor(private builder: FormBuilder, private router: Router) {
    this.ctrlName = new FormControl('', Validators.required);
    this.ctrlPhoneNumber = new FormControl('', Validators.compose([Validators.required, CustomValidators.phoneNumber]));

    this.signupForm = builder.group({
      name: this.ctrlName,
      phoneNumber: this.ctrlPhoneNumber
    });
  }

  signup() {
    this.router.navigate(['sign-up', 'thank-you']);
  }

}
