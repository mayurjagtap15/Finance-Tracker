package com.financeapp.controller;

import com.financeapp.dto.CategoryDto;
import com.financeapp.model.TransactionType;
import com.financeapp.model.User;
import com.financeapp.service.CategoryService;
import com.financeapp.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    private User getUser(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername());
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getUser(userDetails);
        model.addAttribute("categories", categoryService.findAllForUser(user.getId()));
        model.addAttribute("user", user);
        return "categories/list";
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("categoryDto", new CategoryDto());
        model.addAttribute("types", TransactionType.values());
        model.addAttribute("user", getUser(userDetails));
        return "categories/form";
    }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal UserDetails userDetails,
                         @Valid @ModelAttribute("categoryDto") CategoryDto dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        User user = getUser(userDetails);
        if (bindingResult.hasErrors()) {
            model.addAttribute("types", TransactionType.values());
            model.addAttribute("user", user);
            return "categories/form";
        }
        categoryService.save(dto, user.getId());
        redirectAttributes.addFlashAttribute("success", "Category created.");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        User user = getUser(userDetails);
        try {
            categoryService.delete(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Category deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }
}
