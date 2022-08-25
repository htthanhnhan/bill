export interface IProduct {
  id?: number;
  name?: string | null;
  quantity?: number;
  price?: number;
}

export class Product implements IProduct {
  constructor(public id?: number, public name?: string | null, public quantity?: number, public price?: number) {}
}
