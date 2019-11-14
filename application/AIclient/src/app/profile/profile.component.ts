import { Component, OnInit, OnDestroy } from '@angular/core';
import { PositionService } from '../position.service';
import { Archive } from '../archive';
import { IdentityService } from '../identity.service';
import { UrlService } from '../url.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy {

  constructor(private positionService:PositionService,private identityService:IdentityService, private urlService:UrlService) { }

  pageReady:false;
  boughtArchives:Archive[]=[];
  soldArchives:Archive[]=[];
  balance:number=0;
  urlSub:Subscription;
  ngOnInit() {
    if(this.urlService.isUrlsLoaded()==true)
    {
      console.log(this.urlService.isUrlsLoaded());
      this.positionService.loadBoughtArchivesNames().subscribe(e=>{this.boughtArchives=e});
      this.positionService.loadSoldArchivesNames().subscribe(e=>{this.soldArchives=e});
      this.identityService.getUserCredit().subscribe(e=>this.balance=e);
    }
    this.urlSub=this.urlService.getUrlsChanges().subscribe(e=>{
      if(e)
      {
        this.positionService.loadBoughtArchivesNames().subscribe(e=>{this.boughtArchives=e});
        this.positionService.loadSoldArchivesNames().subscribe(e=>{this.soldArchives=e});
        this.identityService.getUserCredit().subscribe(e=>this.balance=e);}
      });
  }

  ngOnDestroy(){
    this.urlSub.unsubscribe();
  }

  //get current user credit
  getPaid()
  {
    this.identityService.resetUserCredit().subscribe(e=>this.balance=0);
  }

  //makes an archive not available anymore
  deleteSoldArchive(item:Archive)
  {
    this.positionService.deleteSoldArchive(item.tagName).subscribe(e=>this.positionService.loadSoldArchivesNames().subscribe(e=>{this.soldArchives=e}));

  }

  //download archive previously published
  downloadSoldArchive(item:Archive)
  {
    this.positionService.loadSoldArchive(item.tagName).subscribe(e=>{console.log(e);this.positionService.downloadFile(e);});
  }

  //download archive previously bought
  downloadBoughtArchive(item:Archive)
  {
    console.log("bhb",item.tagName);
    this.positionService.loadBoughtArchive(item.tagName).subscribe(e=>{console.log(e);this.positionService.downloadFile(e);});;
  }

}
