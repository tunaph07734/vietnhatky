package com.poly.controller;

import com.poly.model.BaiViet;
import com.poly.model.TaiKhoan;
import com.poly.service.BaiVietService;
import com.poly.service.TaiKhoanService;
import com.poly.validate.NhatKyValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
public class GreetingController {
    @Autowired
    private TaiKhoanService taiKhoanService;

    @Autowired
    private BaiVietService baiVietService;
    //validate
    NhatKyValidate nhatKyValidate = new NhatKyValidate();
    //đối tượng sau khi đăng nhập
    TaiKhoan taiKhoan = new TaiKhoan();

    //login
    @GetMapping(value = {"index.html", "/"})
    private String index(ModelMap model) {
        taiKhoanService.findAll();
        model.put("errorLogin", null);
        return "index.html";
    }

    //Đăng nhập
    @PostMapping("login.html")
    private String login(HttpSession session, @RequestParam("tenTaiKhoan") String user, @RequestParam("matKhau") String password,
                         ModelMap model) {

        if (session.getAttribute("userLogin") != null) {
            return "redirect:/";
        } else {

            taiKhoan = taiKhoanService.findByUsernameAndPassword(user, password);
            System.out.println(user + "  " + password);
            if (taiKhoan != null) {
                session.setAttribute("account", taiKhoan);
                return "redirect:/view.html";
            } else {
                model.put("errorLogin", "Sai tài khoản hoặc mật khẩu");
                return "index.html";
            }

        }
    }
    //hiển thị danh sách bài viết theo tentaikhoan
    @GetMapping(value = {"view.html"})
    public ModelAndView listBaiViet() {
        if (taiKhoan==null||taiKhoan.getTenTaiKhoan()==null) {
            ModelAndView modelAndView = new ModelAndView("/index");
            modelAndView.addObject("errorLogin", "Vui Lòng Đăng Nhập");
            return modelAndView;
        }
        List<BaiViet> baiViet = baiVietService.findAllBaiVietByTenDangNhap(taiKhoan.getTenTaiKhoan());
        ModelAndView modelAndView = new ModelAndView("/view");
        modelAndView.addObject("baiViet", baiViet);
        modelAndView.addObject("baiViet1", new BaiViet());
        modelAndView.addObject("taiKhoan", taiKhoan);
        return modelAndView;
    }

    //hiển thị thông tin cá nhân
    @GetMapping(value = {"info.html"})
    public ModelAndView viewInfo() {
        if (taiKhoan==null||taiKhoan.getTenTaiKhoan()==null) {
            ModelAndView modelAndView = new ModelAndView("/index");
            modelAndView.addObject("errorLogin", "Vui Lòng Đăng Nhập");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView("/info");
        modelAndView.addObject("taiKhoan", taiKhoan);
        return modelAndView;
    }

    //tạo bài viết
    @GetMapping("create.html")
    private ModelAndView showTaoBaiViet() {

        if (taiKhoan==null||taiKhoan.getTenTaiKhoan()==null) {
            ModelAndView modelAndView = new ModelAndView("/index");
            modelAndView.addObject("errorLogin", "Vui Lòng Đăng Nhập");
            return modelAndView;
        }

        ModelAndView modelAndView = new ModelAndView("/create");
        BaiViet baiViet = new BaiViet();
        baiViet.setNgay(java.time.LocalDate.now() + "");
        System.out.println(baiViet.getNgay());
        modelAndView.addObject("baiViet", baiViet);
        return modelAndView;
    }

    @PostMapping("create.html")
    private ModelAndView taoBaiViet(@ModelAttribute("baiViet") BaiViet baiViet) {
        baiViet.setNgay(baiViet.getNgay().replace("/", "-"));
        System.out.println(baiViet.getNgay());
        if (!nhatKyValidate.checkSize(1, 50, baiViet.getTieuDe().length())) {
            ModelAndView modelAndView = new ModelAndView("/create");
            modelAndView.addObject("baiViet", new BaiViet());
            modelAndView.addObject("message", "Độ dài tiêu đề từ 1-50 ký tự");
            return modelAndView;
        } else if (!nhatKyValidate.checkSize(1, 50, baiViet.getMoTa().length())) {
            ModelAndView modelAndView = new ModelAndView("/create");
            modelAndView.addObject("baiViet", new BaiViet());
            modelAndView.addObject("message", "Độ dài mô tả từ 1-50 ký tự");
            return modelAndView;
        } else if (!nhatKyValidate.checkSize(1, 5000, baiViet.getNoiDung().length())) {
            ModelAndView modelAndView = new ModelAndView("/create");
            modelAndView.addObject("baiViet", new BaiViet());
            modelAndView.addObject("message", "Độ dài nội dung từ 1-5000 ký tự");
            return modelAndView;
        } else if (!nhatKyValidate.checkSize(1, 500, baiViet.getAnh().length())) {
            ModelAndView modelAndView = new ModelAndView("/create");
            modelAndView.addObject("baiViet", new BaiViet());
            modelAndView.addObject("message", "Độ dài đường dẫn ảnh từ 1-500 ký tự");
            return modelAndView;
        } else
            baiViet.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        if(baiViet.isTrangThai()){
            System.out.println(baiViet.isTrangThai());
        }
        baiVietService.save(baiViet);
        ModelAndView modelAndView = new ModelAndView("/create");
        modelAndView.addObject("baiViet", new BaiViet());
        modelAndView.addObject("message", "Thêm bài viết thành công !");
        return modelAndView;
    }

    //Xóa bài viết
    Optional<BaiViet> baiViet1 = Optional.of(new BaiViet());

    @GetMapping("deletePost/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id) {
        if (taiKhoan==null||taiKhoan.getTenTaiKhoan()==null) {
            ModelAndView modelAndView = new ModelAndView("/index");
            modelAndView.addObject("errorLogin", "Vui Lòng Đăng Nhập");
            return modelAndView;
        }
        baiViet1 = baiVietService.findById(id);
        if (baiViet1 != null) {
            ModelAndView modelAndView = new ModelAndView("/deletePost");
            modelAndView.addObject("baiViet", new BaiViet());
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("deletePost")
    public String deletePost() {
        System.out.println(baiViet1.get().getMaBaiViet());
        baiVietService.remove(baiViet1.get().getMaBaiViet());
        return "redirect:view.html";
    }

    //Sửa bài viết
    Long idpost;

    @GetMapping("/editPost/{id}")
    public ModelAndView showEditForm(@PathVariable Long id) {
        if (taiKhoan==null||taiKhoan.getTenTaiKhoan()==null) {
            ModelAndView modelAndView = new ModelAndView("/index");
            modelAndView.addObject("errorLogin", "Vui Lòng Đăng Nhập");
            return modelAndView;
        }
        baiViet1 = baiVietService.findById(id);
        idpost = baiViet1.get().getMaBaiViet();
        System.out.println(idpost);
        if (baiViet1 != null) {
            ModelAndView modelAndView = new ModelAndView("/editPost");
            modelAndView.addObject("baiViet", baiViet1);
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("/editPost")
    public ModelAndView updateProvince(@ModelAttribute("baiViet") BaiViet baiViet) {
        baiViet.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());

        baiViet.setMaBaiViet(idpost);
        baiViet.setNgay(baiViet.getNgay().replace("/", "-"));
        if (!nhatKyValidate.checkSize(1, 50, baiViet.getTieuDe().length())) {
            ModelAndView modelAndView = new ModelAndView("/editPost");
            modelAndView.addObject("baiViet", new BaiViet());
            modelAndView.addObject("message", "Độ dài tiêu đề từ 1-50 ký tự");
            return modelAndView;
        } else if (!nhatKyValidate.checkSize(1, 50, baiViet.getMoTa().length())) {
            ModelAndView modelAndView = new ModelAndView("/editPost");
            modelAndView.addObject("baiViet", new BaiViet());
            modelAndView.addObject("message", "Độ dài mô tả từ 1-50 ký tự");
            return modelAndView;
        } else if (!nhatKyValidate.checkSize(1, 500, baiViet.getNoiDung().length())) {
            ModelAndView modelAndView = new ModelAndView("/editPost");
            modelAndView.addObject("baiViet", new BaiViet());
            modelAndView.addObject("message", "Độ dài nội dung từ 1-500 ký tự");
            return modelAndView;
        } else if (!nhatKyValidate.checkSize(1, 500, baiViet.getAnh().length())) {
            ModelAndView modelAndView = new ModelAndView("/editPost");
            modelAndView.addObject("baiViet", new BaiViet());
            modelAndView.addObject("message", "Độ dài đường dẫn ảnh từ 1-500 ký tự");
            return modelAndView;
        } else
            baiVietService.save(baiViet);
        baiVietService.remove(idpost);
        ModelAndView modelAndView = new ModelAndView("/editPost");
        modelAndView.addObject("baiViet", baiViet);
        modelAndView.addObject("message", "Sửa Bài Viết Thành Công");
        return modelAndView;
    }

    //view bài biết
    @GetMapping("/viewPost/{id}")
    public ModelAndView viewPost(@PathVariable Long id) {
        Optional<BaiViet> baiViet = baiVietService.findById(id);
        if (baiViet != null) {
            ModelAndView modelAndView = new ModelAndView("/viewPost");
            modelAndView.addObject("baiViet", baiViet);
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    //Đăng Xuất
    @GetMapping(value = {"logout.html"})
    public ModelAndView logout(ModelMap model) {

        taiKhoan = null;
        ModelAndView modelAndView = new ModelAndView("/index");
        model.put("errorLogin", "Đã Đăng Xuất");
        return modelAndView;
    }

    //đổi mật khẩu
    @GetMapping(value = {"changePass.html"})
    private String changePass(ModelMap model) {
        if (taiKhoan==null||taiKhoan.getTenTaiKhoan()==null) {
            model.put("errorLogin", null);
            return "index.html";
        }
        model.put("errorChangePass", null);
        return "changePass.html";
    }

    @PostMapping("changePass.html")
    private String changePass(@RequestParam("matKhauCu") String matKhauCu, @RequestParam("matKhauMoi") String matKhauMoi, @RequestParam("nhapLai") String nhapLai, ModelMap model) {
        if (!matKhauCu.equals(taiKhoan.getMatKhau())) {
            model.put("errorChangePass", "Mật Khẩu Cũ Sai");
            return "changePass.html";
        } else if (!matKhauMoi.equals(nhapLai)) {
            model.put("errorChangePass", "Mật Khẩu Nhập Lại Phải Giống Mật Khẩu Cũ");
            return "changePass.html";
        } else
            taiKhoan.setMatKhau(matKhauMoi);
        taiKhoanService.save(taiKhoan);
        model.put("errorChangePass", "Đổi Mật Khẩu Thành Công");
        return "changePass.html";

    }

    //đăng ký
    @GetMapping(value = {"registration.html"})
    private ModelAndView showRegistration() {
        ModelAndView modelAndView = new ModelAndView("/registration");
        modelAndView.addObject("taiKhoan", new TaiKhoan());
        return modelAndView;
    }

    @PostMapping("registration.html")
    private ModelAndView registration(@ModelAttribute("taiKhoan") TaiKhoan taiKhoan) {
        System.out.println(taiKhoan.toString());
        taiKhoanService.save(taiKhoan);
        ModelAndView modelAndView = new ModelAndView("/registration");
        modelAndView.addObject("taiKhoan", new TaiKhoan());
        modelAndView.addObject("errorLogin", "Dang Ky Thanh Cong !");
        return modelAndView;
    }

//    tìm kiếm theo tên bài viết
    @PostMapping("search.html")
    private ModelAndView search(@ModelAttribute("baiViet1") BaiViet baiViet) {
        System.out.println(baiViet.getTieuDe());
        List<BaiViet> baiViet1 = baiVietService.findAllBaiVietByTenTieuDe(baiViet.getTieuDe());
        ModelAndView modelAndView = new ModelAndView("/view");
        modelAndView.addObject("baiViet", baiViet1);
        modelAndView.addObject("taiKhoan", taiKhoan);
        return modelAndView;

    }
    // bài viết công khai
    @GetMapping(value = {"public.html"})
    public ModelAndView listBaiVietCongKhai() {
        List<BaiViet> baiViet = baiVietService.findAllBaiVietByTrangThai();
        ModelAndView modelAndView = new ModelAndView("/public");
        modelAndView.addObject("baiViet", baiViet);
        modelAndView.addObject("baiViet1", new BaiViet());
        return modelAndView;
    }

}

