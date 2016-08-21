/* tslint:disable:no-unused-variable */

import { TestBed, inject          } from '@angular/core/testing/test_bed';
import { FormsModule, FormBuilder } from '@angular/forms';

import { CourseAddComponent } from './course-add.component';
import { CommitteeService   } from '../../committee/committee.service';

describe('Component: CourseAddComponent', () => {

  it('should create an instance', inject([CourseAddComponent], (component: CourseAddComponent) => {
    expect(component).toBeTruthy();
  }));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      providers: [
        CommitteeService,
        CourseAddComponent,
        FormBuilder
      ]
    });
  });

});
