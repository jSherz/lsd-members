import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { ISignupService, SignupResult } from './index';

export class SignupServiceStub implements ISignupService {

    static validName: string = "Abby North";
    static validEmail: string = "AbbyNorth@teleworm.us";
    static validPhoneNumber: string = "07058074719";

    static inUseName: string = "Mohammed Charlton";
    static inUseEmail: string = "MohammedCharlton@jourrapide.com";
    static inUsePhoneNumber: string = "07985457231";

    static apiFailName: string = "Amy Farrell";
    static apiFailEmail: string = "AmyFarrell@armyspy.com";
    static apiFailPhoneNumber: string = "07827651140";

    signup(name: string, email?: string, phoneNumber?: string): Observable<SignupResult> {
        if (name == SignupServiceStub.validName && (email == SignupServiceStub.validEmail || phoneNumber == SignupServiceStub.validPhoneNumber)) {
            return Observable.of(new SignupResult(true, undefined));
        } else if (name == SignupServiceStub.inUseName && email == SignupServiceStub.inUseEmail) {
            return Observable.of(new SignupResult(false, [
                { 'email': 'The specified e-mail is in use' }
            ]));
        } else if (name == SignupServiceStub.inUseName && phoneNumber == SignupServiceStub.inUsePhoneNumber) {
            return Observable.of(new SignupResult(false, [
                { 'phoneNumber': 'The specified phone number is in use' }
            ]));
        } else if (name == SignupServiceStub.apiFailName && (email == SignupServiceStub.apiFailEmail || phoneNumber == SignupServiceStub.apiFailPhoneNumber)) {
            return Observable.throw("Internal server error");
        } else {
            return Observable.throw("Unknown details used with stub");
        }
    }

}
