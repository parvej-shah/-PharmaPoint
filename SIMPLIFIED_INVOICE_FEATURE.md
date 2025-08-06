
# Simplified Invoice Generation (PharmaPoint)

**PharmaPoint now generates professional, text-based invoices for every sale.**

---

## Key Points

- Clean, readable `.txt` invoices (no PDF, no HTML)
- All details: pharmacy, patient, medicines, totals
- Files saved in `invoices/` folder, named by invoice ID and date
- Opens and prints with any text editor
- No dependencies, no corruption issues

---

## Example Invoice
```
================================================================================
                           INVOICE
================================================================================

Invoice ID: 12345
Date: 02/08/2025 20:05:02

PHARMACY DETAILS:
--------------------------------------------------
Name: Green Life Pharmacy
Area: Dhanmondi

PATIENT DETAILS:
--------------------------------------------------
Name: John Doe
Phone: 01712345678

MEDICINES:
--------------------------------------------------------------------------------
Medicine                       Brand           Qty      Unit Price   Subtotal    
--------------------------------------------------------------------------------
Paracetamol                    Square          2        5.00         10.00       
Aspirin                        Bayer           1        12.00        12.00       
--------------------------------------------------------------------------------
                                                             Total: 22.00 BDT
                                                             Items: 3

================================================================================
                     Thank you for your business!
================================================================================
```

---

## How it Works

1. Sell medicines and enter patient info
2. Confirm sale
3. System saves invoice as `.txt` in `invoices/` folder
4. User gets success message and can open/print the file

---

## Why This is Better

- No PDF corruption, no extra software needed
- Fast, reliable, and universally compatible
- Easy to print, share, and archive
- Simple to maintain and extend

---

**Production ready. No more PDF headaches—just clean, professional invoices!**
