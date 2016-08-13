import { Component                 } from '@angular/core';
import { Router, ROUTER_DIRECTIVES } from '@angular/router';
import { HTTP_PROVIDERS            } from '@angular/http';
import {
  FormControl,
  FormBuilder,
  FormGroup,
  Validators,
  REACTIVE_FORM_DIRECTIVES
} from '@angular/forms';
import { CustomValidators                 } from '../utils';
import { SignupService, SignupServiceImpl } from './signup.service';

@Component({
  templateUrl: 'signup-alt.component.html',
  providers: [
    HTTP_PROVIDERS,
    { provide: SignupService, useClass: SignupServiceImpl }
  ],
  directives: [ROUTER_DIRECTIVES, REACTIVE_FORM_DIRECTIVES]
})
export class SignupAltComponent {

  signupForm: FormGroup;

  ctrlName: FormControl;
  ctrlEmail: FormControl;

  constructor(private builder: FormBuilder, private router: Router, private signupService: SignupService) {
    this.ctrlName = new FormControl('', Validators.required);
    this.ctrlEmail = new FormControl('', Validators.compose([Validators.required, CustomValidators.email]));

    this.signupForm = builder.group({
      name: this.ctrlName,
      email: this.ctrlEmail
    });
  }

  signup(user) {
    this.signupService.signupAlt(user.name, user.email).subscribe(
      result => {
        console.log(result);

        this.router.navigate(['sign-up', 'thank-you']);
      },
      error => console.log(error)
    );
  }

}
