import { Component, OnInit } from "@angular/core";
import { TextMessagesService } from "./text-messages.service";
import { TextMessage } from "./text-message";

@Component({
  selector: "lsd-received-messages",
  templateUrl: "./received-messages.component.html",
  styleUrls: ["./received-messages.component.sass"]
})
export class ReceivedMessagesComponent implements OnInit {
  receivedMessages: TextMessage[] = null;

  constructor(private service: TextMessagesService) {}

  ngOnInit() {
    this.service
      .getReceivedMessages()
      .subscribe(r => (this.receivedMessages = r));
  }
}
