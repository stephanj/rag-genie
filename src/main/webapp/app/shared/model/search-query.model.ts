export interface ISearchQuery {
  query?: string;
  totalResults?: number;
}
export class SearchQuery implements ISearchQuery {
  constructor(
    public query?: string,
    public totalResults?: number
  ) {
  }
}
