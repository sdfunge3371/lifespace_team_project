package com.lifespace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lifespace")
public class FrontendPageController {

    //前台
    @GetMapping("/orders")
    public String toOrders(){
        return "front-end/orders";
    }

    @GetMapping("/homepage")
    public String toHomePage(){
        return "front-end/homepage";
    }

    @GetMapping("/login")
    public String toLogin(){
        return "/front-end/login";
    }


    @GetMapping("/frontend_faq")
    public String toFaq(){
        return "/front-end/frontend_faq";
    }

    @GetMapping("/frontend_news")
    public String toNews(){
        return "/front-end/frontend_news";
    }

    @GetMapping("comments_frontend")
    public String toComments(){
        return "/front-end/comments_frontend";
    }

    @GetMapping("/event_create")
    public String toEventCreate(){
        return "/front-end/event_create";
    }

    @GetMapping("/event_detail")
    public String toEventDetail(){
        return "/front-end/event_detail";
    }

    @GetMapping("/event_overview")
    public String toEventOverview(){
        return "/front-end/event_overview";
    }

    @GetMapping("/events_for_user")
    public String toEventsForUser(){
        return "/front-end/events_for_user";
    }

    @GetMapping("/favorite_space")
    public String toFavoriteSpace(){
        return "/front-end/favorite_space";
    }

    @GetMapping("/individual_space")
    public String toIndividualSpace(){
        return "/front-end/individual_space";
    }

    @GetMapping("/myAccount")
    public String toMyAccount(){
        return "/front-end/myAccount";
    }

        @GetMapping("/password")
    public String toPassword(){
        return "/front-end/password";
    }
        @GetMapping("/payment_fail")
    public String toPaymentFail(){
        return "/front-end/payment_fail";
    }
        @GetMapping("/payment_loading")
    public String toPaymentLoading(){
        return "/front-end/payment_loading";
    }
        @GetMapping("/payment_success")
    public String toPaymentSuccess(){
        return "/front-end/payment_success";
    }
        @GetMapping("/register")
    public String toRegister(){
        return "/front-end/register";
    }
        @GetMapping("/setPasswordOK")
    public String toSetPassword(){
        return "/front-end/setPasswordOK";
    }
        @GetMapping("/spaceoverview")
    public String toSpaceOverview(){
        return "/front-end/spaceoverview";
    }

//
//    //後台
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
//    @GetMapping("/")
//    public String toHomePage(){
//        return "/front-end/homepage";
//    }
//
}

