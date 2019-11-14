import {Injectable, OnInit} from '@angular/core'
import { IdentityService } from './identity.service';
import {CanActivate, RouterStateSnapshot, ActivatedRoute, RouterState} from "@angular/router";
import {Router,ActivatedRouteSnapshot} from '@angular/router'
import { PositionService } from './position.service';
import { Position } from './position';

//checks if the user can navigate to the search page
@Injectable()
export class SearchGuard implements CanActivate
{
    constructor(private identityService: IdentityService, private router: Router, private positionService:PositionService) {}
    canActivate(route: ActivatedRouteSnapshot,state: RouterStateSnapshot): boolean
    {

        if (this.identityService.isUserLogged())
        {
           return true;
        }
        else
        {
            this.router.navigate(['/login']);
            return false;
        }
    }

}
