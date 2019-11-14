import {Injectable, OnInit} from '@angular/core'
import { IdentityService } from './identity.service';
import {CanActivate, RouterStateSnapshot, ActivatedRoute, RouterState} from "@angular/router";
import {Router,ActivatedRouteSnapshot} from '@angular/router'
import { PositionService } from './position.service';
import { Position } from './position';

//checks if the user can navigate to the purchase page
@Injectable()
export class PurchaseGuard implements CanActivate
{
    constructor(private identityService: IdentityService, private router: Router, private positionService:PositionService) {}
    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): boolean
    {
        let userId=this.identityService.getCurrentUsername();
        if (this.identityService.isUserLogged())
        {
           if(state.url==`/login/USER/${userId}/purchase`)
            {
                if(this.positionService.getSelectedArchives()!=null)
                {
                    return true;
                }
                else
                {
                    this.router.navigate([`/login/USER/${userId}`]);
                    return false;
                }
            }
            else
            {
                this.router.navigate([`/home`]);
                return false;
            }
        }
        else
        {
            this.router.navigate(['/login']);
            return false;
        }
    }

}
