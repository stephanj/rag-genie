
export interface IDocumentWithEmbeddings {
  text?: string;
  embedding?: number[];
}

export class DocumentWithEmbeddings implements IDocumentWithEmbeddings {
  constructor(
    public text?: string,
    public embedding?: number[]) {
  }
}
