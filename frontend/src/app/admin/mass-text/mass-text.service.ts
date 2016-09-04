import { Injectable } from '@angular/core';


export abstract class MassTextService {
}


@Injectable()
export class MassTextServiceImpl extends MassTextService {

  constructor() {
    super();
  }

}
