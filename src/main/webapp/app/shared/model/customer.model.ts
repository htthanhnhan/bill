export interface ICustomer {
  id?: number;
  fullName?: string;
  email?: string;
  address?: string | null;
}

export class Customer implements ICustomer {
  constructor(public id?: number, public fullName?: string, public email?: string, public address?: string | null) {}
}
