export class GameInfo {
  private _name: string = "";

  get name(): string {
    return this._name;
  }

  private _imageUrl: string = "";

  get imageUrl(): string {
    return this._imageUrl;
  }

  private _id: number = 0;

  get id(): number {
    return this._id;
  }
}
