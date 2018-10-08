import { NgModule, ErrorHandler } from "@angular/core";
import * as Raven from "raven-js";

Raven.config(
  "https://719d5087702e45d8ba95925cbb94c973@sentry.io/154262"
).install();

export class RavenErrorHandler implements ErrorHandler {
  handleError(err: any): void {
    Raven.captureException(err.originalError);
  }
}
