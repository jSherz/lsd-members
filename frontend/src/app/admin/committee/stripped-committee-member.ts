/**
 * A committee member with the bare minimum of information.
 */
export class StrippedCommitteeMember {
  uuid: string;
  name: string;

  constructor (uuid: string, name: string) {
    this.uuid = uuid;
    this.name = name;
  }
}
