import { AbstractControl, ValidatorFn } from '@angular/forms';
import { PhoneNumberUtil, PhoneNumber } from 'google-libphonenumber';

type ValidationResult = {
  [key: string]: boolean;
};

export class CustomValidators {

  private static phoneNumberUtil = PhoneNumberUtil.getInstance();

  private static defaultRegion = 'GB';

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

  /**
   * Use Google's libphonenumber library to validate phone numbers.
   */
  static phoneNumber(control: AbstractControl): ValidationResult {
    let numToTest: string = control.value;

    if (numToTest != undefined && numToTest != '') {
      try {
        let parsed = CustomValidators.phoneNumberUtil.parse(numToTest, CustomValidators.defaultRegion);

        if (CustomValidators.phoneNumberUtil.isPossibleNumber(parsed) &&
            CustomValidators.phoneNumberUtil.isValidNumber(parsed)) {
          return null;
        } else {
          return { 'phoneNumber': true };
        }
      } catch (ex) {
        return { 'phoneNumber': true };
      }
    } else {
      return { 'phoneNumber': true };
    }
  }

}
