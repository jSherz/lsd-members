import {Component, OnInit} from '@angular/core';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators
} from '@angular/forms';
import {Router} from '@angular/router';
import * as moment from 'moment';

import {MassTextService} from './mass-text.service';


@Component({
  selector: 'lsd-mass-text',
  templateUrl: './mass-text.component.html',
  styleUrls: ['./mass-text.component.sass']
})
export class MassTextComponent implements OnInit {

  /**
   * An opt-out message sent at the end of each text.
   *
   * @type {string}
   */
  private optOut = ' - Reply \'NOFUN\' to stop these messages';

  /**
   * Maximum message size is three standard text messages.
   *
   * @type {number}
   */
  private maxTextLength: number = 160 * 3;

  /**
   * Most names are likely to be below this size.
   *
   * @type {string}
   */
  private exampleLongName = 'Dr. Daniel Stevenson DVM';

  /**
   * Matches names in templates.
   *
   * @type {RegExp}
   */
  private nameRegex: RegExp = new RegExp('\{\{ *name *\}\}', 'g');

  massTextForm: FormGroup;

  ctrlStartDate: FormControl;
  ctrlEndDate: FormControl;
  ctrlSendAfterDate: FormControl;
  ctrlTemplate: FormControl;

  preview = '';

  /**
   * Set when an API request fails.
   *
   * @type {boolean}
   */
  apiRequestFailed = false;

  /**
   * Should we show the loading animation?
   *
   * @type {boolean}
   */
  showThrobber = false;

  numCharsUsed = 0;
  numCharsTotal = this.maxTextLength;

  /**
   * Any error returned by the API.
   */
  error: string = null;

  /**
   * Build the course add form, including setting up any validation.
   *
   * @param builder
   * @param router
   * @param service
   */
  constructor(private builder: FormBuilder, private router: Router, private service: MassTextService) {
    const todayFormatted = moment().format('YYYY-MM-DD');
    const tomorrowFormatted = moment().add(1, 'days').format('YYYY-MM-DD');
    const anHourFromNow = moment().add(1, 'hours').format('YYYY-MM-DDTHH:mm:ss');

    this.ctrlStartDate = new FormControl(todayFormatted, Validators.required);
    this.ctrlEndDate = new FormControl(tomorrowFormatted, Validators.required);
    this.ctrlSendAfterDate = new FormControl(anHourFromNow, Validators.required);
    this.ctrlTemplate = new FormControl('Hello, {{ name }}', Validators.required);

    this.massTextForm = builder.group({
      startDate: this.ctrlStartDate,
      endDate: this.ctrlEndDate,
      sendAfterDate: this.ctrlSendAfterDate,
      template: this.ctrlTemplate
    });
  }

  updateTemplate() {
    this.preview = this.replaceNameInMessage(this.ctrlTemplate.value, 'Mary') + this.optOut;

    this.numCharsUsed = this.replaceNameInMessage(this.ctrlTemplate.value, this.exampleLongName).length +
      this.optOut.length;
  }

  ngOnInit() {
    this.updateTemplate();
  }

  send(data) {
    this.showThrobber = true;

    this.service.send(data.startDate, data.endDate, data.template, this.preview).subscribe(
      result => {
        // API request succeeded, check result
        this.apiRequestFailed = false;
        this.showThrobber = false;

        if (result.success) {
          this.router.navigate(['courses', 'calendar']);
        } else {
          this.error = result.error;
        }
      },
      error => {
        // API request failed, show generic error
        console.log('Sending mass text failed: ' + error);

        this.apiRequestFailed = true;
        this.showThrobber = false;
      }
    );
  }

  /**
   * Replace instances of {{ name }} with the given value.
   *
   * @param template
   * @param value
   */
  private replaceNameInMessage(template: string, value: string): string {
    return template.replace(this.nameRegex, value);
  }

}
