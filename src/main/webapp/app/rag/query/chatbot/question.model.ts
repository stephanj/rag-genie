export class ChatQuestion {
  constructor(
    public cfp: string,
    public userId: number,
    public question: string) {}
}

export class ChatResponse {

  constructor(public answer: string) {
  }
}
