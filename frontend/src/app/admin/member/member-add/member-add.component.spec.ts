/* tslint:disable:no-unused-variable */

import { TestBed, async } from "@angular/core/testing";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";

import { MemberAddComponent } from "./member-add.component";
import { StubMemberService } from "../member.service.stub";

describe("Component: MemberAdd", () => {
  function mockComp(): MemberAddComponent {
    const keys = [];
    for (const key in Router.prototype) {
      if (Router.prototype.hasOwnProperty(key)) {
        keys.push(key);
      }
    }

    const builder = new FormBuilder();
    const router = jasmine.createSpyObj("MockRouter", keys);
    const service = new StubMemberService();

    return new MemberAddComponent(builder, service, router);
  }

  it("should create an instance", async(() => {
    const component = mockComp();

    expect(component).toBeTruthy();
  }));
});
