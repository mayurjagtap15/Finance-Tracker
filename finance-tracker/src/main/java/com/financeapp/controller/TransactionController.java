package com.financeapp.controller;

import com.financeapp.dto.TransactionDto;
import com.financeapp.model.Transaction;
import com.financeapp.model.TransactionType;
import com.financeapp.model.User;
import com.financeapp.service.CategoryService;
import com.financeapp.service.TransactionService;
import com.financeapp.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final UserService userService;

    private User getUser(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername());
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        User user = getUser(userDetails);
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.findAllByUser(user.getId(), pageable);
        model.addAttribute("transactions", transactions);
        model.addAttribute("user", user);
        return "transactions/list";
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getUser(userDetails);
        model.addAttribute("transactionDto", new TransactionDto());
        model.addAttribute("categories", categoryService.findAllForUser(user.getId()));
        model.addAttribute("types", TransactionType.values());
        model.addAttribute("user", user);
        return "transactions/form";
    }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal UserDetails userDetails,
                         @Valid @ModelAttribute("transactionDto") TransactionDto dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        User user = getUser(userDetails);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllForUser(user.getId()));
            model.addAttribute("types", TransactionType.values());
            model.addAttribute("user", user);
            return "transactions/form";
        }
        transactionService.save(dto, user.getId());
        redirectAttributes.addFlashAttribute("success", "Transaction added successfully.");
        return "redirect:/transactions";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable Long id, Model model) {
        User user = getUser(userDetails);
        Transaction transaction = transactionService.findByIdAndUserId(id, user.getId());
        model.addAttribute("transactionDto", transactionService.toDto(transaction));
        model.addAttribute("categories", categoryService.findAllForUser(user.getId()));
        model.addAttribute("types", TransactionType.values());
        model.addAttribute("user", user);
        return "transactions/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long id,
                         @Valid @ModelAttribute("transactionDto") TransactionDto dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        User user = getUser(userDetails);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllForUser(user.getId()));
            model.addAttribute("types", TransactionType.values());
            model.addAttribute("user", user);
            return "transactions/form";
        }
        transactionService.update(id, dto, user.getId());
        redirectAttributes.addFlashAttribute("success", "Transaction updated.");
        return "redirect:/transactions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetails userDetails,
                         @PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        User user = getUser(userDetails);
        transactionService.delete(id, user.getId());
        redirectAttributes.addFlashAttribute("success", "Transaction deleted.");
        return "redirect:/transactions";
    }
}
