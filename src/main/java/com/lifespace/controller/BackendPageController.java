package com.lifespace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class BackendPageController {

        //前台
    @GetMapping("/orders")
    public String toBackEndOrders(){
        return "/back-end/orders";
    }
    
    @GetMapping("/admin")
    public String toAdmin(){
        return "/back-end/admin";
    }

    @GetMapping("/add_branch")
    public String toAddBranch(){
        return "/back-end/add_branch";
    }
    @GetMapping("/addSpace")
    public String toAddSpace(){
        return "/back-end/addSpace";
    }
    @GetMapping("/add")
    public String toAdminAdd(){
        return "/back-end/adminAdd";
    }
    @GetMapping("/update")
    public String toAdminUpdate(){
        return "/back-end/adminUpdate";
    }

    @GetMapping("/addNews")
    public String toAddNews(){
        return "/back-end/backend_addnews";
    }

    @GetMapping("/faq")
    public String toFaq(){
        return "/back-end/backend_faq";
    }

    @GetMapping("/index")
    public String toIndex(){
        return "/back-end/backend_index";
    }

    @GetMapping("/news")
    public String toNews(){
        return "/back-end/backend_news";
    }

    @GetMapping("/updatenews")
    public String toUpdatenews(){
        return "/back-end/backend_updatenews";
    }

    @GetMapping("/branch")
    public String toBranch(){
        return "/back-end/branch";
    }
    
    @GetMapping("/backend_index")
    public String toBackendIndex(){
        return "/back-end/backend_index";
    }

    @GetMapping("/chatroom_management")
    public String toChatroom(){
        return "/back-end/chatroom_management";
    }

    @GetMapping("/comment_report_backend")
    public String toCommentReportBackend(){
        return "/back-end/comment_report_backend";
    }

    @GetMapping("/comments_backend")
    public String toCommentsBackend(){
        return "/back-end/comments_backend";
    }

    @GetMapping("/edit_branch")
    public String toEditBranch(){
        return "/back-end/edit_branch";
    }

    @GetMapping("/listSpaces")
    public String toListSpaces(){
        return "/back-end/listSpaces";
    }

    @GetMapping("/loginAdmin")
    public String tologinAdmin(){
        return "/back-end/loginAdmin";
    }

    @GetMapping("/member")
    public String toMember(){
        return "/back-end/member";
    }

    @GetMapping("/memberadd")
    public String toMemberAdd(){
        return "/back-end/MemberAdd";
    }

    @GetMapping("/memberupdate")
    public String toMemberUpdate(){
        return "/back-end/memberupdate";
    }

    @GetMapping("/rental_item")
    public String toRentalItem(){
        return "/back-end/rental_item";
    }

    @GetMapping("/space_comment")
    public String toSpaceComment(){
        return "/back-end/space_comment";
    }

    @GetMapping("/updateSpace")
    public String toUpdateSpace(){
        return "/back-end/updateSpace";
    }
}
