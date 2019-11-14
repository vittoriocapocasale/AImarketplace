package ai.marketplace.server.common;

import ai.marketplace.server.structures.Pair;
import ai.marketplace.server.structures.Position;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Functions
{
    //get authentication information
    public static Authentication getCurrentAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    //Untested! function to find compatible positions. Quadratic in the number of positions,
    //and selection is based on "arbitrary" criteria (speed<speedMax). For these reasons unused.
    public static ArrayList<Position> findMaximumCompatiblePositions(ArrayList<Position> originalList)
    {
        TreeSet<Pair<Position, Long>> compatibilityMap= new TreeSet<>((a, b)->Long.compare(b.second,a.second));
        List<Position> validPositions=originalList.stream().filter(Position::valid).collect(Collectors.toList());
        ArrayList<Position> bestPositions=new ArrayList<>();
        for (Position p:validPositions)
        {
            Long total=0L;
            for(Position q:validPositions)
            {
                if(p!=q)
                {
                    total=total+p.compatibility(q);
                }

            }
            compatibilityMap.add(new Pair<Position, Long>(p,total));
        }
        for (Pair<Position, Long> c:compatibilityMap)
        {
            Boolean insert = true;
            for (Position p:bestPositions)
            {
                if(!c.first.isCompatible(p))
                {

                    insert=false;
                }
            }
            if (insert)
            {
                bestPositions.add(c.first);
            }
        }
        bestPositions.sort(Position::compareTo);
        return bestPositions;
    }


    //all the links in this service
    public static List<String> getAllHateoasLinks()
    {
        Authentication auth=Functions.getCurrentAuthentication();
        String user="{user}";
        if (auth!=null && auth.isAuthenticated()&&!(auth instanceof AnonymousAuthenticationToken))
        {
            user=auth.getName();

        }
        List<String> links= new ArrayList<>();
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("oauth/token").build().toUri().toASCIIString(), "token"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("admins").build().toUri().toASCIIString(), "admins"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("admins/{admin}").build().toUri().toASCIIString(), "admin"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("clients").build().toUri().toASCIIString(), "clients"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("clients/{client}").build().toUri().toASCIIString(), "client"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("users").build().toUri().toASCIIString(), "users"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("users/{user}").buildAndExpand(user).toUri().toASCIIString(), "user"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("users/{user}/credit").buildAndExpand(user).toUri().toASCIIString(), "credit"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("users/{user}/soldArchives").buildAndExpand(user).toUri().toASCIIString(), "soldArchives"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("users/{user}/soldArchives/{soldArchive}").buildAndExpand(user,"{soldArchive}").toUri().toASCIIString(), "soldArchive"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("users/{user}/boughtArchives").buildAndExpand(user).toUri().toASCIIString(), "boughtArchives"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("users/{user}/boughtArchives/{boughtArchive}").buildAndExpand(user,"{boughtArchive}").toUri().toASCIIString(), "boughtArchive"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("marketplace/positions/{?params*}").build().toUri().toASCIIString(), "positions"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("marketplace/positions/count/{?params*}").build().toUri().toASCIIString(), "count"));
        links.add(LinkUtil.createLinkHeader(ServletUriComponentsBuilder.fromCurrentContextPath().path("marketplace/archives/{?params*}").build().toUri().toASCIIString(), "archives"));
        return links;
    }

    //rounds double to places digits
    public static double floatRounder(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
