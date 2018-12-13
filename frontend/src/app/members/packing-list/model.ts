export class PackingListItems {
  warmClothes: boolean;
  sleepingBag: boolean;
  sturdyShoes: boolean;
  cash: boolean;
  toiletries: boolean;

  constructor(
    warmClothes: boolean,
    sleepingBag: boolean,
    sturdyShoes: boolean,
    cash: boolean,
    toiletries: boolean
  ) {
    this.warmClothes = warmClothes;
    this.sleepingBag = sleepingBag;
    this.sturdyShoes = sturdyShoes;
    this.cash = cash;
    this.toiletries = toiletries;
  }
}
