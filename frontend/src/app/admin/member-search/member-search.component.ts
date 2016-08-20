import { Component, OnInit } from '@angular/core';
import {Observable} from "rxjs";
import {SearchResult, MemberSearchService} from "./member-search.service";

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

  searchTerm: string = '';

  /**
   * Has the user performed a search yet?
   *
   * @type {boolean}
   */
  hasSearched: boolean = false;

  searchResults: SearchResult[] = [];

  constructor(private searchService: MemberSearchService) { }

  ngOnInit() {
  }

  performSearch() {
    this.hasSearched = true;

    this.searchService.search(this.searchTerm).subscribe(results => {
      this.searchResults = results;
    });
  }

  selectResult(result) {
    console.log('Selected: ');
    console.log(result);
  }

}
