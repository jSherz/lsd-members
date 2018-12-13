import { NgModule } from "@angular/core";

import { AppModule } from "./app.module";
import { MemberSearchComponent } from "./admin/member/member-search/member-search.component";
import { MemberService } from "./admin/member/member.service";
import { StubMemberService } from "./admin/member/member.service.stub";
import { APP_BASE_HREF } from "@angular/common";
import { CommitteeService } from "./admin/committee/committee.service";
import { StubCommitteeService } from "./admin/committee/committee.service.stub";
import { AdminModule } from "./admin/admin.module";

@NgModule({
  imports: [AppModule, AdminModule],
  declarations: [],
  bootstrap: [],
  providers: [
    { provide: APP_BASE_HREF, useValue: "/" },
    { provide: CommitteeService, useClass: StubCommitteeService },
    { provide: MemberService, useClass: StubMemberService },
    MemberSearchComponent
  ]
})
export class TestModule {}
