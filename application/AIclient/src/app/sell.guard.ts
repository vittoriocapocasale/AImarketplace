import {Injectable, OnInit} from '@angular/core'
import { IdentityService } from './identity.service';
import {CanActivate, RouterStateSnapshot, ActivatedRoute, RouterState} from "@angular/router";
import {Router,ActivatedRouteSnapshot} from '@angular/router'
import { PositionService } from './position.service';
import { Position } from './position';

//checks if the user can navigate to the selling page
@Injectable()
export class SellGuard implements CanActivate
{
    constructor(private identityService: IdentityService, private router: Router) {}
    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): boolean
    {
        let userId=this.identityService.getCurrentUsername();
        let userRole=this.identityService.getCurrentUserRole();
        if (this.identityService.isUserLogged())
        {
           if(state.url==`/login/USER/${userId}/sell`)
            {
                return true;
            }
            else
            {
                this.router.navigate([`/login/${userRole}/${userId}`]);
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
