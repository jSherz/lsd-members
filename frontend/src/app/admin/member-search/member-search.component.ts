import {Component, OnInit, Output, EventEmitter} from '@angular/core';

import { SearchResult, MemberSearchService } from './member-search.service';

/**
 * Enables a user to search for a member and then select that member.
 *
 * Returns that selection to another part of the system.
 */
@Component({
  selector: 'lsd-member-search',
  templateUrl: 'member-search.component.html'
})
export class MemberSearchComponent implements OnInit {

  /**
   * Number of members to show, each time the "more results" button is clicked.
   *
   * @type {number}
   */
  displayPerLoad = 10;

  searchTerm: string = '';

  /**
   * Has the user performed a search yet?
   *
   * @type {boolean}
   */
  hasSearched: boolean = false;

  /**
   * All of the returned results.
   *
   * @type {Array}
   */
  searchResults: SearchResult[] = [];

  /**
   * The results currently being displayed.
   *
   * @type {Array}
   */
  displayedSearchResults: SearchResult[] = [];

  /**
   * Used to send parent components selected member events.
   *
   * @type {EventEmitter}
   */
  @Output() memberSelected: EventEmitter<SearchResult> = new EventEmitter<SearchResult>();

  constructor(private searchService: MemberSearchService) { }

  ngOnInit() {
  }

  performSearch() {
    this.hasSearched = true;

    this.searchService.search(this.searchTerm).subscribe(results => {
      this.searchResults = results;
      this.displayedSearchResults = results.slice(0, this.displayPerLoad);
    });
  }

  /**
   * Called when the user clicks on a member. Emits an event to inform listening components that a member was chosen.
   *
   * @param result
   */
  searchResultSelected(result) {
    result.chosen = true;

    this.memberSelected.emit(result);
  }

  /**
   * Display some more search results, if available.
   */
  showMoreResults() {
    if (!this.allResultsDisplayed()) {
      let startIndex = this.displayedSearchResults.length;
      let maxIndex = startIndex + this.displayPerLoad;

      // Keep displaying more results while we have them
      for (let i = startIndex; i < maxIndex && i < this.searchResults.length; i++) {
        this.displayedSearchResults.push(this.searchResults[i]);
      }
    }
  }

  /**
   * Have all of the available results been displayed?
   */
  allResultsDisplayed(): boolean {
    return this.displayedSearchResults.length >= this.searchResults.length;
  }

}
