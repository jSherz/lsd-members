import { Component } from '@angular/core';
import { Router    } from '@angular/router';
import {
  FormControl,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';

import { CustomValidators } from '../../utils';
import { SignupService    } from '../service/signup.service';

@Component({
  selector: 'signup-component',
  templateUrl: 'signup.component.html'
})
export class SignupComponent {

  signupForm: FormGroup;

  ctrlName: FormControl;
  ctrlPhoneNumber: FormControl;

  /**
   * Set when an API request fails.
   *
   * @type {boolean}
   */
  apiRequestFailed: boolean = false;

  /**
   * Should we show the loading animation?
   *
   * @type {boolean}
   */
  showThrobber: boolean = false;

  /**
   * Any errors returned by the API.
   */
  errors: {
    name: undefined,
    email: undefined
  };

  /**
   * Build the sign-up form, including setting up any validation.
   *
   * @param builder
   * @param router
   * @param signupService
   */
  constructor(private builder: FormBuilder, private router: Router, private signupService: SignupService) {
    this.ctrlName = new FormControl('', Validators.required);
    this.ctrlPhoneNumber = new FormControl('', Validators.compose([Validators.required, CustomValidators.phoneNumber]));

    this.signupForm = builder.group({
      name: this.ctrlName,
      phoneNumber: this.ctrlPhoneNumber
    });
  }

  /**
   * Attempt to sign-up a user by name and phone number.
   *
   * Will show an error message if the API call fails or if it returns an error.
   *
   * Navigates to the "thank-you" page if signing up succeeds.
   *
   * @param user (with params "name" and "phoneNumber")
   */
  signup(user) {
    this.showThrobber = true;

    this.signupService.signup(user.name, user.phoneNumber).subscribe(
      result => {
        // API request succeeded, check result
        this.apiRequestFailed = false;
        this.showThrobber = false;

        if (result.success) {
          this.router.navigate(['sign-up', 'thank-you']);
        } else {
          this.errors = result.errors;
        }
      },
      error => {
        // API request failed, show generic error
        console.log('Sign-up alt failed: ' + error);

        this.apiRequestFailed = true;
        this.showThrobber = false;
      }
    );
  }

  /**
   * Translates an error message key received from the server.
   *
   * @param key
   * @returns {any}
   */
  translate(key: string) {
    return {
      'error.inUse': 'This phone number is in use. Have you already signed up?'
    }[key];
  }

}
