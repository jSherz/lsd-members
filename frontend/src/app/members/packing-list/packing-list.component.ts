import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormControl, FormGroup } from "@angular/forms";

import { PackingListService } from "./packing-list.service";
import { PackingListItems } from "./model";

@Component({
  selector: "lsd-packing-list",
  templateUrl: "./packing-list.component.html",
  styleUrls: ["./packing-list.component.sass"]
})
export class PackingListComponent implements OnInit {
  packingListForm: FormGroup;

  ctrlWarmClothes: FormControl;
  ctrlSleepingBag: FormControl;
  ctrlSturdyShoes: FormControl;
  ctrlCash: FormControl;
  ctrlToiletries: FormControl;

  packingListItems: PackingListItems;

  loadingPackingListFailed: boolean;

  constructor(
    private service: PackingListService,
    private builder: FormBuilder
  ) {
    this.ctrlWarmClothes = new FormControl("");
    this.ctrlSleepingBag = new FormControl("");
    this.ctrlSturdyShoes = new FormControl("");
    this.ctrlCash = new FormControl("");
    this.ctrlToiletries = new FormControl("");

    this.packingListForm = builder.group({
      warmClothes: this.ctrlWarmClothes,
      sleepingBag: this.ctrlSleepingBag,
      sturdyShoes: this.ctrlSturdyShoes,
      cash: this.ctrlCash,
      toiletries: this.ctrlToiletries
    });
  }

  ngOnInit() {
    this.service.getPackingList().subscribe(
      items => {
        this.packingListForm.setValue(items);
        this.setupSubscription();
      },
      error => {
        console.error("Failed to load packing list items:", error);
        this.loadingPackingListFailed = true;
      }
    );
  }

  private setupSubscription() {
    this.packingListForm.valueChanges.subscribe(items => {
      this.service.putPackingList(items).subscribe(response => {
        console.log("Updated packing list items", response);
      });
    });
  }
}
