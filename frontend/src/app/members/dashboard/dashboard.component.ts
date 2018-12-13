import { Component, OnInit } from "@angular/core";
import { DashboardService } from "./dashboard.service";
import { BasicInfo } from "./basic-info";

@Component({
  selector: "lsd-members-dashboard",
  templateUrl: "dashboard.component.html",
  styleUrls: ["dashboard.component.sass"]
})
export class DashboardComponent implements OnInit {
  basicInfo: BasicInfo = null;

  dashboardLoadFailed = false;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.dashboardService.getBasicInfo().subscribe(
      (basicInfo: BasicInfo) => {
        this.basicInfo = basicInfo;
      },
      error => {
        console.log("Failed to load dashboard:", error);
        this.dashboardLoadFailed = true;
      }
    );
  }
}
