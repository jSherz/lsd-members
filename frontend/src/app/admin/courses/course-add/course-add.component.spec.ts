/* tslint:disable:no-unused-variable */

import { TestBed, inject          } from '@angular/core/testing/test_bed';
import { FormsModule, FormBuilder } from '@angular/forms';

import { CourseAddComponent } from './course-add.component';

describe('Component: CourseAddComponent', () => {

  it('should create an instance', inject([CourseAddComponent], (component: CourseAddComponent) => {
    expect(component).toBeTruthy();
  }));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      providers: [
        CourseAddComponent,
        FormBuilder
      ]
    });
  });

});
