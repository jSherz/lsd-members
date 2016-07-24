import { AbstractControl, ValidatorFn } from '@angular/forms';

type ValidationResult = {
  [key: string]: boolean;
};

export class CustomValidators {

  /**
   * Perform a basic sanity check on an e-mail address.
   */
  static email(control: AbstractControl): ValidationResult {
    if (control.value == undefined || control.value == '' || !/.+\@.+\..+/.test(control.value)) {
      return { 'email': true };
    } else {
      return null;
    }
  }

}
