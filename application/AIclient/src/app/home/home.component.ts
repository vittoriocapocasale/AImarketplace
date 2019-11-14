import { Component, OnInit } from '@angular/core';
import { UrlService } from '../url.service';
import {Router} from '@angular/router'

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})

//simple component containing only links to login/registration pages
export class HomeComponent implements OnInit {

  constructor(private urlService:UrlService, private router:Router) {}

  ngOnInit() {
  }
  test()
  {
    console.log("testing...")
    this.urlService.updateUrls();
  }
  goToLogin()
  {
    this.router.navigate(["/login"]);
  }
  goToRegistration()
  {
    this.router.navigate(["/registration"]);
  }
}
