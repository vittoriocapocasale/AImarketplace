import { Component, OnInit } from '@angular/core';
import { Archive } from '../archive';
import { PositionService } from '../position.service';
import { Router } from '@angular/router';
import { IdentityService } from '../identity.service';
import { ThrowStmt } from '@angular/compiler';

@Component({
  selector: 'app-purchase',
  templateUrl: './purchase.component.html',
  styleUrls: ['./purchase.component.css']
})
export class PurchaseComponent implements OnInit {

  constructor(private positionService:PositionService,private router:Router, private identityService:IdentityService) { }

  ownedArchives:string[]=null;
  neededArchives:string[]=null;
  purchaseS:boolean=false;
  purchaseN:boolean=false;
  ngOnInit() {
    this.neededArchives=this.positionService.getSelectedArchives();
    this.positionService.loadBoughtArchivesNames().subscribe(r=>{this.ownedArchives=r.map(e=>e.tagName); this.neededArchives=this.neededArchives.filter(e=>
      {
        for(let i=0; i<this.ownedArchives.length;i++)
        {
          if(e==this.ownedArchives[i])
          {
            return false;
          }
        }
        return true;
      })
    }, e=>this.ownedArchives=[]);
  }

  //performs purchase of the needed archives
  purchase()
  {
    this.purchaseS=false;
    this.purchaseN=false;
    this.positionService.buyArchives(this.neededArchives).subscribe(e=>{
      this.purchaseS=true;
      this.neededArchives.forEach(e=>this.positionService.loadBoughtArchive(e).subscribe(a=>this.positionService.downloadFile(a)))
    }, e=>this.purchaseN=true);
  }

  //abort buy operation and go to profile
  cancel()
  {
    this.positionService.setSelectedArchives(null);
    if(this.identityService.isUserLogged())
    {
      this.router.navigate([`/login/${this.identityService.getCurrentUserRole()}/${this.identityService.getCurrentUsername()}`]);
    }
    else
    {
      this.router.navigate([`/login`]);
    }
  }
}
