import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import {Response} from '@angular/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';

import {PackingListComponent} from './packing-list.component';
import {PackingListService} from './packing-list.service';
import {PackingListItems} from './model';
import {StubPackingListService} from './packing-list-service.stub';

describe('PackingListComponent', () => {

  let component: PackingListComponent;
  let fixture: ComponentFixture<PackingListComponent>;

  let getResult: Observable<PackingListItems>;
  let putResult: Observable<Response>;

  let service: PackingListService;

  const serviceFactory = () => {
    service = new StubPackingListService(getResult, putResult);
    return service;
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PackingListComponent],
      imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
      ],
      providers: [
        {provide: PackingListService, useFactory: serviceFactory}
      ]
    }).compileComponents();
  }));

  const createComponent = () => {
    fixture = TestBed.createComponent(PackingListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  it('should display the packing list components when loaded', async(() => {
    const exampleItems = new PackingListItems(false, true, false, true, true);
    getResult = of(exampleItems);

    createComponent();

    fixture.whenStable().then(() => {
      const formValue = component.packingListForm.getRawValue();

      expect(formValue.warmClothes).toEqual(exampleItems.warmClothes);
      expect(formValue.sleepingBag).toEqual(exampleItems.sleepingBag);
      expect(formValue.sturdyShoes).toEqual(exampleItems.sturdyShoes);
      expect(formValue.cash).toEqual(exampleItems.cash);
      expect(formValue.toiletries).toEqual(exampleItems.toiletries);

      expect(component.loadingPackingListFailed).toBeFalsy();
    });
  }));

  it('should show an error when loading the packing list items fails', async(() => {
    getResult = throwError(new Error('Some loading error 40404'));

    createComponent();

    fixture.whenStable().then(() => {
      expect(component.loadingPackingListFailed).toBeTruthy();
    });
  }));

  it('updates the packing list items when checked', async(() => {
    getResult = of({
      warmClothes: false,
      sleepingBag: false,
      sturdyShoes: false,
      cash: false,
      toiletries: false
    });
    putResult = of(null);

    createComponent();
    const putSpy = spyOn(service, 'putPackingList').and.callThrough();

    fixture.whenStable().then(() => {
      const items = {
        warmClothes: true,
        sleepingBag: true,
        sturdyShoes: false,
        cash: false,
        toiletries: true
      };

      component.packingListForm.setValue(items, {emitEvent: true});

      expect(putSpy).toHaveBeenCalledWith(items);
    });
  }));

});
