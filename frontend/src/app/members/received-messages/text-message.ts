import * as moment from "moment";

/**
 * This is a clone of the TextMessage model in the admin Webpack module.
 */
export class TextMessage {
  uuid: string;
  memberUuid: string;
  massTextUuid: string;
  status: number;
  toNumber: string;
  fromNumber: string;
  message: string;
  externalId: string;
  createdAt: moment.Moment;
  updatedAt: moment.Moment;

  constructor(
    uuid: string,
    memberUuid: string,
    massTextUuid: string,
    status: number,
    toNumber: string,
    fromNumber: string,
    message: string,
    externalId: string,
    createdAt: moment.Moment,
    updatedAt: moment.Moment
  ) {
    this.uuid = uuid;
    this.memberUuid = memberUuid;
    this.massTextUuid = massTextUuid;
    this.status = status;
    this.toNumber = toNumber;
    this.fromNumber = fromNumber;
    this.message = message;
    this.externalId = externalId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
