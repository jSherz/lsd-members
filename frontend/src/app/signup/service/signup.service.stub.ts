import { Observable                  } from 'rxjs/Observable';
import { SignupResult, SignupService } from '../index';

export class SignupServiceStub extends SignupService {

  static validName: string = 'Abby North';
  static validEmail: string = 'AbbyNorth@teleworm.us';
  static validPhoneNumber: string = '07058074719';

  static inUseName: string = 'Mohammed Charlton';
  static inUseEmail: string = 'MohammedCharlton@jourrapide.com';
  static inUsePhoneNumber: string = '07985457231';

  static apiFailName: string = 'Amy Farrell';
  static apiFailEmail: string = 'AmyFarrell@armyspy.com';
  static apiFailPhoneNumber: string = '07827651140';

  signup(name: string, phoneNumber: string): Observable<SignupResult> {
    if (name === SignupServiceStub.validName && phoneNumber === SignupServiceStub.validPhoneNumber) {
      return Observable.of(new SignupResult(true, {}));
    } else if (name === SignupServiceStub.inUseName && phoneNumber === SignupServiceStub.inUsePhoneNumber) {
      return Observable.of(new SignupResult(false, [
        { 'phoneNumber': 'The specified phone number is in use' }
      ]));
    } else if (name === SignupServiceStub.apiFailName && phoneNumber === SignupServiceStub.apiFailPhoneNumber) {
      return Observable.throw('Internal server error');
    } else {
      return Observable.throw('Unknown details used with stub');
    }
  }

  signupAlt(name: string, email: string): Observable<SignupResult> {
    if (name === SignupServiceStub.validName && email === SignupServiceStub.validEmail) {
      return Observable.of(new SignupResult(true, {}));
    } else if (name === SignupServiceStub.inUseName && email === SignupServiceStub.inUseEmail) {
      return Observable.of(new SignupResult(false, [
        { 'email': 'The specified e-mail is in use' }
      ]));
    } else if (name === SignupServiceStub.apiFailName && email === SignupServiceStub.apiFailEmail) {
      return Observable.throw('Internal server error');
    } else {
      return Observable.throw('Unknown details used with stub');
    }
  }

}
