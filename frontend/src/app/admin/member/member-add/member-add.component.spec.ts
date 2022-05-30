/* eslint-disable @typescript-eslint/no-unused-vars */

import { TestBed, async } from "@angular/core/testing";
import { FormBuilder } from "@angular/forms";
import { Router } from "@angular/router";

import { MemberAddComponent } from "./member-add.component";
import { StubMemberService } from "../member.service.stub";

describe("Component: MemberAdd", () => {
  function mockComp(): MemberAddComponent {
    const keys = ["navigate"];

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
