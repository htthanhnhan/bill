export interface IBill {
  id?: number;
  products?: string | null;
  customer?: string | null;
  total?: number | null;
  JProduct?: any[] | null;
}

export class Bill implements IBill {
  constructor(public id?: number, public products?: string | null, public customer?: string | null, public total?: number | null, public JProduct?: any[] | null) {}
}
