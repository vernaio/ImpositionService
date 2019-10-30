import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Version } from './version';

@Component({
  selector: 'app-version',
  templateUrl: './version.component.html',
  styleUrls: ['./version.component.css']
})
export class VersionComponent implements OnInit {

  version: Version = null;

  /**
   * Custom constructor.
   */
  constructor(
    private http: HttpClient
  ) { }

  /**
   * Is called after initialization of the class.
   */
  ngOnInit() {
    this.loadVersion();
  }

  /**
   * Load the version details.
   */
  loadVersion() {
    this.http.get<Version>("/version")
      .subscribe((data: Version) => this.version = data);
  }

}