import {NgModule}      from '@angular/core';

import {AppModule} from './app.module';
import {MemberSearchComponent} from './admin/member/member-search/member-search.component';
import {MemberService} from './admin/member/member.service';
import {StubMemberService} from './admin/member/member.service.stub';
import {APP_BASE_HREF} from '@angular/common';
import {CommitteeService} from './admin/committee/committee.service';
import {StubCommitteeService} from './admin/committee/committee.service.stub';

@NgModule({
  imports: [
    AppModule
  ],
  declarations: [],
  bootstrap: [],
  providers: [
    {provide: APP_BASE_HREF, useValue: '/'},
    {provide: CommitteeService, useValue: new StubCommitteeService()},
    {provide: MemberService, useValue: new StubMemberService()},
    MemberSearchComponent
  ]
})
export class TestModule {
}
