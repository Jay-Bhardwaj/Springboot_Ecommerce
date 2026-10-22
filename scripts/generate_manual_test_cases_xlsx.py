from __future__ import annotations

from datetime import UTC, datetime
from pathlib import Path
from xml.sax.saxutils import escape
import zipfile


ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "Manual_Test_Cases_Ecommerce.xlsx"


HEADERS = [
    "Test Case ID",
    "Module/Category",
    "Test Case Name",
    "Objective",
    "Prerequisites",
    "Test Steps",
    "Expected Result",
    "Actual Result",
    "Status",
]


TEST_CASES = [
    {
        "id": "TC-01",
        "module": "1. User Authentication & Profile Management",
        "name": "Customer Registration (Happy Path)",
        "objective": "Verify a new customer can register with a unique Gmail address and valid password.",
        "prerequisites": "Frontend running on http://localhost:3000 and backend on http://localhost:8080. Use a fresh Gmail address not already present in the database.",
        "steps": [
            "Open http://localhost:3000.",
            "Switch to the customer registration form.",
            "Enter a full name, unique Gmail address, and password containing letters, numbers, and a special character.",
            "Click Register Customer.",
        ],
        "expected": [
            "Registration succeeds and a success toast is displayed.",
            "The user is redirected back to the customer login view.",
            "The new account is created with CUSTOMER role.",
        ],
    },
    {
        "id": "TC-02",
        "module": "1. User Authentication & Profile Management",
        "name": "Customer Registration with Duplicate Email",
        "objective": "Verify duplicate email registration is blocked.",
        "prerequisites": "A customer account already exists with the selected Gmail address.",
        "steps": [
            "Open the customer registration form.",
            "Enter the same Gmail address that already exists in the system.",
            "Submit the registration form.",
        ],
        "expected": [
            "Registration is rejected.",
            "An error toast/message indicates the email is already registered.",
            "No duplicate account is created.",
        ],
    },
    {
        "id": "TC-03",
        "module": "1. User Authentication & Profile Management",
        "name": "Registration Email Validation",
        "objective": "Verify registration only accepts email addresses ending with @gmail.com.",
        "prerequisites": "Application is reachable from the browser.",
        "steps": [
            "Open the customer registration form.",
            "Enter a non-Gmail email such as user@yahoo.com with other valid details.",
            "Submit the form.",
        ],
        "expected": [
            "Registration is rejected.",
            "An error message states that the email must contain @gmail.com.",
        ],
    },
    {
        "id": "TC-04",
        "module": "1. User Authentication & Profile Management",
        "name": "Registration Password Validation",
        "objective": "Verify registration rejects passwords that do not contain letters, numbers, and a special character.",
        "prerequisites": "Application is reachable from the browser.",
        "steps": [
            "Open the customer registration form.",
            "Enter a valid Gmail address and a weak password such as passwordonly or 12345678.",
            "Submit the form.",
        ],
        "expected": [
            "Registration is rejected.",
            "An error message states the password must contain alphabet, number, and special character.",
        ],
    },
    {
        "id": "TC-05",
        "module": "1. User Authentication & Profile Management",
        "name": "Customer Login (Happy Path)",
        "objective": "Verify a valid customer can log in successfully.",
        "prerequisites": "A CUSTOMER account exists with known credentials.",
        "steps": [
            "Open http://localhost:3000.",
            "Stay on Customer sign in.",
            "Enter a valid customer email and password.",
            "Click Login as Customer.",
        ],
        "expected": [
            "Login succeeds and a success toast is displayed.",
            "The dashboard/catalog view opens.",
            "Products are loaded from the backend.",
            "Session values are stored in localStorage: token, userRole, userName, and userEmail.",
        ],
    },
    {
        "id": "TC-06",
        "module": "1. User Authentication & Profile Management",
        "name": "Customer Login with Invalid Password",
        "objective": "Verify invalid credentials are rejected.",
        "prerequisites": "A CUSTOMER account exists.",
        "steps": [
            "Open the customer login form.",
            "Enter a valid customer email and an incorrect password.",
            "Click Login as Customer.",
        ],
        "expected": [
            "Login fails.",
            "An error message indicates invalid credentials.",
            "The user remains on the login page and no session is persisted.",
        ],
    },
    {
        "id": "TC-07",
        "module": "1. User Authentication & Profile Management",
        "name": "Admin Login with Seeded Credentials",
        "objective": "Verify the seeded admin account can access the admin dashboard.",
        "prerequisites": "Backend started with seeded admin available: admin@shop.com / Admin@123.",
        "steps": [
            "Open the application.",
            "Switch to Admin access.",
            "Enter admin@shop.com and Admin@123.",
            "Click Login as Admin.",
        ],
        "expected": [
            "Admin login succeeds and a success toast is displayed.",
            "The admin dashboard opens.",
            "Product management actions become available.",
        ],
    },
    {
        "id": "TC-08",
        "module": "1. User Authentication & Profile Management",
        "name": "Admin Account Attempted via Customer Login",
        "objective": "Verify admin credentials are blocked from the customer login flow.",
        "prerequisites": "Seeded admin account exists.",
        "steps": [
            "Open Customer sign in.",
            "Enter admin@shop.com and Admin@123.",
            "Click Login as Customer.",
        ],
        "expected": [
            "Login is rejected for the customer flow.",
            "An error message tells the user to use the admin option for admin accounts.",
        ],
    },
    {
        "id": "TC-09",
        "module": "1. User Authentication & Profile Management",
        "name": "Forgot Password (Happy Path)",
        "objective": "Verify a customer can reset the password with a valid Gmail account.",
        "prerequisites": "A customer account exists with a Gmail address.",
        "steps": [
            "Open Customer sign in and click Forgot password.",
            "Enter the existing customer Gmail address.",
            "Enter a new valid password containing letters, numbers, and a special character.",
            "Click Reset Password.",
            "Return to login and sign in with the new password.",
        ],
        "expected": [
            "Password reset succeeds and a success toast is displayed.",
            "The app returns to the customer login view.",
            "Login with the new password succeeds.",
        ],
    },
    {
        "id": "TC-10",
        "module": "1. User Authentication & Profile Management",
        "name": "Forgot Password for Unknown Email",
        "objective": "Verify password reset rejects unknown customer emails.",
        "prerequisites": "Application is reachable from the browser.",
        "steps": [
            "Open the password reset view.",
            "Enter a Gmail address not present in the system.",
            "Enter a valid new password and submit.",
        ],
        "expected": [
            "Password reset is rejected.",
            "An error message indicates no customer was found with this email.",
        ],
    },
    {
        "id": "TC-11",
        "module": "1. User Authentication & Profile Management",
        "name": "Session Persistence After Browser Refresh",
        "objective": "Verify a logged-in session is restored after refreshing the page.",
        "prerequisites": "User is already logged in as CUSTOMER or ADMIN.",
        "steps": [
            "Log in successfully.",
            "Refresh the browser tab.",
            "Observe the application state after reload.",
        ],
        "expected": [
            "The session is restored automatically from localStorage.",
            "The user remains logged in.",
            "Dashboard data reloads without forcing the user back to the login page.",
        ],
    },
    {
        "id": "TC-12",
        "module": "1. User Authentication & Profile Management",
        "name": "Profile Update (Happy Path)",
        "objective": "Verify a customer can update name and Gmail address from the profile page.",
        "prerequisites": "Customer is logged in.",
        "steps": [
            "Open the profile page from the dashboard.",
            "Edit the full name and Gmail address with unique valid values.",
            "Click Edit Profile.",
        ],
        "expected": [
            "Profile update succeeds.",
            "A success toast is displayed.",
            "Updated name/email appear in the profile view and session storage is refreshed.",
        ],
    },
    {
        "id": "TC-13",
        "module": "1. User Authentication & Profile Management",
        "name": "Profile Update with Duplicate Email",
        "objective": "Verify the profile page blocks updating the email to one already registered by another account.",
        "prerequisites": "Customer is logged in and another account already uses the target Gmail address.",
        "steps": [
            "Open the profile page.",
            "Change the email to a Gmail address already used by a different account.",
            "Click Edit Profile.",
        ],
        "expected": [
            "Profile update fails.",
            "An error message indicates the email is already registered.",
            "Existing profile values remain unchanged.",
        ],
    },
    {
        "id": "TC-14",
        "module": "2. Product Catalog & Discovery",
        "name": "Product Catalog Load for Customer",
        "objective": "Verify the customer dashboard displays products retrieved from the backend.",
        "prerequisites": "Customer is logged in and at least one product exists in the database.",
        "steps": [
            "Log in as a customer.",
            "Wait for the dashboard to load.",
            "Review the product grid/list.",
        ],
        "expected": [
            "Products are displayed on the dashboard.",
            "Each product shows core information such as name, price, and category.",
            "No product-loading error toast appears on a healthy backend.",
        ],
    },
    {
        "id": "TC-15",
        "module": "2. Product Catalog & Discovery",
        "name": "Search and Category Filter",
        "objective": "Verify customers can filter products by search keyword and category.",
        "prerequisites": "Customer is logged in and the database contains products from multiple categories.",
        "steps": [
            "Open the customer dashboard.",
            "Type a product keyword in the search field.",
            "Select a category filter.",
            "Optionally set minimum and maximum price values.",
        ],
        "expected": [
            "Only products matching the entered search term, selected category, and price range remain visible.",
            "Resetting filters returns the full product list.",
        ],
    },
    {
        "id": "TC-16",
        "module": "2. Product Catalog & Discovery",
        "name": "Product Detail View",
        "objective": "Verify a customer can open product details from the dashboard.",
        "prerequisites": "Customer is logged in and at least one product exists.",
        "steps": [
            "Open the customer dashboard.",
            "Select a product card.",
            "Review the product details panel/page.",
        ],
        "expected": [
            "The selected product details are displayed.",
            "The content matches the chosen product and the panel can be closed cleanly.",
        ],
    },
    {
        "id": "TC-17",
        "module": "3. Cart Management",
        "name": "Add In-Stock Product to Cart",
        "objective": "Verify a customer can add an available product to the cart.",
        "prerequisites": "Customer is logged in and at least one product has stock quantity greater than 0.",
        "steps": [
            "Open the customer dashboard.",
            "Choose an in-stock product.",
            "Click Add to Cart.",
        ],
        "expected": [
            "The product is added to the cart.",
            "A success toast is displayed.",
            "Cart item count and bill summary update immediately.",
        ],
    },
    {
        "id": "TC-18",
        "module": "3. Cart Management",
        "name": "Add Out-of-Stock Product to Cart",
        "objective": "Verify out-of-stock products cannot be added to the cart.",
        "prerequisites": "Customer is logged in and at least one product has stock quantity 0.",
        "steps": [
            "Open the customer dashboard.",
            "Attempt to add an out-of-stock product to the cart.",
        ],
        "expected": [
            "The product is not added to the cart.",
            "An error toast indicates the product is currently out of stock.",
        ],
    },
    {
        "id": "TC-19",
        "module": "3. Cart Management",
        "name": "Update Cart Quantity Beyond Available Stock",
        "objective": "Verify cart quantity cannot exceed backend stock.",
        "prerequisites": "Customer is logged in and has a cart item with limited stock available.",
        "steps": [
            "Add a product to the cart.",
            "Increase the quantity beyond the product stock quantity.",
            "Observe the cart response.",
        ],
        "expected": [
            "The quantity is not saved above available stock.",
            "An error message is shown when the backend rejects the requested quantity.",
        ],
    },
    {
        "id": "TC-20",
        "module": "3. Cart Management",
        "name": "Remove Product from Cart",
        "objective": "Verify a customer can remove an item from the cart.",
        "prerequisites": "Customer is logged in and at least one product is already in the cart.",
        "steps": [
            "Open the cart section on the dashboard.",
            "Remove a product or reduce its quantity to zero.",
        ],
        "expected": [
            "The item is removed from the cart.",
            "A success toast confirms removal.",
            "Cart totals update correctly.",
        ],
    },
    {
        "id": "TC-21",
        "module": "4. Checkout & Orders",
        "name": "Checkout with Cash on Delivery",
        "objective": "Verify a customer can place an order using COD with a non-empty cart.",
        "prerequisites": "Customer is logged in and cart contains at least one in-stock item.",
        "steps": [
            "Proceed to checkout from the dashboard.",
            "Enter customer name, phone number, address line 1, city, state, and postal code.",
            "Keep payment method as Cash on Delivery.",
            "Click Place Order.",
        ],
        "expected": [
            "Order placement succeeds and an order confirmation screen is shown.",
            "An order number is generated in the format ORD-YYYYMMDD-XXXXXXXX.",
            "Payment status is shown as PENDING for COD.",
            "Cart is cleared after successful checkout.",
        ],
    },
    {
        "id": "TC-22",
        "module": "4. Checkout & Orders",
        "name": "Checkout with Online Payment",
        "objective": "Verify payment status becomes PAID when checkout uses ONLINE payment mode.",
        "prerequisites": "Customer is logged in and cart contains at least one in-stock item.",
        "steps": [
            "Proceed to checkout.",
            "Fill all required address fields.",
            "Select Online Payment.",
            "Place the order.",
        ],
        "expected": [
            "Order placement succeeds.",
            "Payment method is stored as ONLINE.",
            "Payment status is shown as PAID.",
        ],
    },
    {
        "id": "TC-23",
        "module": "4. Checkout & Orders",
        "name": "Checkout Required Field Validation",
        "objective": "Verify checkout rejects missing mandatory delivery fields.",
        "prerequisites": "Customer is logged in and cart contains at least one item.",
        "steps": [
            "Open checkout.",
            "Leave one or more required fields blank, such as phone number or city.",
            "Click Place Order.",
        ],
        "expected": [
            "Order placement is blocked.",
            "Required field validation prevents submission or the backend returns a clear bad-request message.",
        ],
    },
    {
        "id": "TC-24",
        "module": "4. Checkout & Orders",
        "name": "Saved Address After Successful Checkout",
        "objective": "Verify the latest checkout address is saved and shown on the profile page.",
        "prerequisites": "Customer is logged in and has successfully placed at least one order.",
        "steps": [
            "Complete a checkout successfully.",
            "Return to the dashboard.",
            "Open the profile page or saved address view.",
        ],
        "expected": [
            "The latest checkout address appears in the Saved address section.",
            "Saved values include customer name, phone number, address, city, state, and postal code.",
        ],
    },
    {
        "id": "TC-25",
        "module": "4. Checkout & Orders",
        "name": "Order History Display",
        "objective": "Verify placed orders are listed in My Orders with status, amount, items, and delivery details.",
        "prerequisites": "Customer is logged in and has at least one previously placed order.",
        "steps": [
            "Open My Orders from the dashboard.",
            "Review the order cards and order details.",
        ],
        "expected": [
            "Orders are listed in reverse chronological order.",
            "Each order shows order number, status, payment method, payment status, amount, items, and delivery address.",
            "Order progress milestones render based on the current order status.",
        ],
    },
    {
        "id": "TC-26",
        "module": "5. Admin Product Management",
        "name": "Admin Create Product",
        "objective": "Verify an admin can create a new product.",
        "prerequisites": "Admin is logged in.",
        "steps": [
            "Open the admin dashboard.",
            "Enter product name, description, price, category, image URL, and stock quantity.",
            "Click the save/create product action.",
        ],
        "expected": [
            "Product creation succeeds.",
            "A success toast is displayed.",
            "The new product appears in the product list after refresh.",
        ],
    },
    {
        "id": "TC-27",
        "module": "5. Admin Product Management",
        "name": "Admin Update Product",
        "objective": "Verify an admin can edit an existing product.",
        "prerequisites": "Admin is logged in and at least one product already exists.",
        "steps": [
            "Open the admin dashboard.",
            "Select an existing product to edit.",
            "Change one or more fields such as price or stock quantity.",
            "Save the product.",
        ],
        "expected": [
            "Product update succeeds.",
            "A success toast is displayed.",
            "The updated values appear in the product list.",
        ],
    },
    {
        "id": "TC-28",
        "module": "5. Admin Product Management",
        "name": "Admin Delete Product",
        "objective": "Verify an admin can delete an existing product.",
        "prerequisites": "Admin is logged in and at least one product already exists.",
        "steps": [
            "Open the admin dashboard.",
            "Delete a product from the product list.",
        ],
        "expected": [
            "Product deletion succeeds.",
            "A success toast is displayed.",
            "The product disappears from the product list.",
            "Associated cart items for that product are cleaned up by the backend.",
        ],
    },
]


def col_letter(index: int) -> str:
    result = ""
    while index:
        index, remainder = divmod(index - 1, 26)
        result = chr(65 + remainder) + result
    return result


def inline_string_cell(ref: str, value: str, style: int | None = None) -> str:
    style_attr = f' s="{style}"' if style is not None else ""
    safe_value = escape(value)
    return f'<c r="{ref}" t="inlineStr"{style_attr}><is><t xml:space="preserve">{safe_value}</t></is></c>'


def build_sheet_xml() -> str:
    rows = []
    sheet_name = "Manual Test Cases"

    rows.append(
        '<row r="1" ht="26" customHeight="1">'
        + inline_string_cell("A1", sheet_name, 2)
        + "</row>"
    )

    header_cells = []
    for idx, header in enumerate(HEADERS, start=1):
        header_cells.append(inline_string_cell(f"{col_letter(idx)}2", header, 1))
    rows.append('<row r="2" ht="24" customHeight="1">' + "".join(header_cells) + "</row>")

    for row_index, case in enumerate(TEST_CASES, start=3):
        values = [
            case["id"],
            case["module"],
            case["name"],
            case["objective"],
            case["prerequisites"],
            "\n".join(f"{step_index}. {step}" for step_index, step in enumerate(case["steps"], start=1)),
            "\n".join(f"- {line}" for line in case["expected"]),
            "",
            "",
        ]

        cells = [
            inline_string_cell(f"{col_letter(col_index)}{row_index}", value, 0)
            for col_index, value in enumerate(values, start=1)
        ]
        rows.append(f'<row r="{row_index}" ht="90" customHeight="1">{"".join(cells)}</row>')

    max_row = len(TEST_CASES) + 2

    cols = [
        (1, 1, 14),
        (2, 2, 28),
        (3, 3, 30),
        (4, 4, 34),
        (5, 5, 36),
        (6, 6, 46),
        (7, 7, 42),
        (8, 8, 28),
        (9, 9, 16),
    ]
    col_xml = "".join(
        f'<col min="{start}" max="{end}" width="{width}" customWidth="1"/>'
        for start, end, width in cols
    )

    merges = '<mergeCells count="1"><mergeCell ref="A1:I1"/></mergeCells>'

    return f'''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <dimension ref="A1:I{max_row}"/>
  <sheetViews>
    <sheetView workbookViewId="0">
      <pane ySplit="2" topLeftCell="A3" activePane="bottomLeft" state="frozen"/>
      <selection pane="bottomLeft" activeCell="A3" sqref="A3"/>
    </sheetView>
  </sheetViews>
  <sheetFormatPr defaultRowHeight="18"/>
  <cols>{col_xml}</cols>
  <sheetData>
    {''.join(rows)}
  </sheetData>
  <autoFilter ref="A2:I{max_row}"/>
  {merges}
  <pageMargins left="0.7" right="0.7" top="0.75" bottom="0.75" header="0.3" footer="0.3"/>
</worksheet>'''


def build_styles_xml() -> str:
    return '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="3">
    <font>
      <sz val="11"/>
      <name val="Calibri"/>
      <family val="2"/>
    </font>
    <font>
      <b/>
      <color rgb="FFFFFFFF"/>
      <sz val="11"/>
      <name val="Calibri"/>
      <family val="2"/>
    </font>
    <font>
      <b/>
      <color rgb="FFFFFFFF"/>
      <sz val="14"/>
      <name val="Calibri"/>
      <family val="2"/>
    </font>
  </fonts>
  <fills count="4">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FF0B5EA8"/><bgColor indexed="64"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FF2F7D32"/><bgColor indexed="64"/></patternFill></fill>
  </fills>
  <borders count="2">
    <border>
      <left/><right/><top/><bottom/><diagonal/>
    </border>
    <border>
      <left style="thin"><color rgb="FFD9E2F3"/></left>
      <right style="thin"><color rgb="FFD9E2F3"/></right>
      <top style="thin"><color rgb="FFD9E2F3"/></top>
      <bottom style="thin"><color rgb="FFD9E2F3"/></bottom>
      <diagonal/>
    </border>
  </borders>
  <cellStyleXfs count="1">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
  </cellStyleXfs>
  <cellXfs count="3">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="1" xfId="0" applyBorder="1" applyAlignment="1">
      <alignment vertical="top" wrapText="1"/>
    </xf>
    <xf numFmtId="0" fontId="1" fillId="2" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
      <alignment horizontal="center" vertical="center" wrapText="1"/>
    </xf>
    <xf numFmtId="0" fontId="2" fillId="3" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
      <alignment horizontal="center" vertical="center"/>
    </xf>
  </cellXfs>
  <cellStyles count="1">
    <cellStyle name="Normal" xfId="0" builtinId="0"/>
  </cellStyles>
</styleSheet>'''


def build_workbook_xml() -> str:
    return '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
          xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Manual Test Cases" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>'''


def build_workbook_rels_xml() -> str:
    return '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>'''


def build_root_rels_xml() -> str:
    return '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
</Relationships>'''


def build_content_types_xml() -> str:
    return '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>'''


def build_app_xml() -> str:
    return '''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"
            xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
  <Application>Microsoft Excel</Application>
  <DocSecurity>0</DocSecurity>
  <ScaleCrop>false</ScaleCrop>
  <HeadingPairs>
    <vt:vector size="2" baseType="variant">
      <vt:variant><vt:lpstr>Worksheets</vt:lpstr></vt:variant>
      <vt:variant><vt:i4>1</vt:i4></vt:variant>
    </vt:vector>
  </HeadingPairs>
  <TitlesOfParts>
    <vt:vector size="1" baseType="lpstr">
      <vt:lpstr>Manual Test Cases</vt:lpstr>
    </vt:vector>
  </TitlesOfParts>
  <Company></Company>
  <LinksUpToDate>false</LinksUpToDate>
  <SharedDoc>false</SharedDoc>
  <HyperlinksChanged>false</HyperlinksChanged>
  <AppVersion>16.0300</AppVersion>
</Properties>'''


def build_core_xml() -> str:
    created = datetime.now(UTC).replace(microsecond=0).isoformat().replace("+00:00", "Z")
    return f'''<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
                   xmlns:dc="http://purl.org/dc/elements/1.1/"
                   xmlns:dcterms="http://purl.org/dc/terms/"
                   xmlns:dcmitype="http://purl.org/dc/dcmitype/"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dc:title>Manual Test Cases - Ecommerce Project</dc:title>
  <dc:creator>Codex</dc:creator>
  <cp:lastModifiedBy>Codex</cp:lastModifiedBy>
  <dcterms:created xsi:type="dcterms:W3CDTF">{created}</dcterms:created>
  <dcterms:modified xsi:type="dcterms:W3CDTF">{created}</dcterms:modified>
</cp:coreProperties>'''


def write_workbook() -> None:
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(OUTPUT, "w", compression=zipfile.ZIP_DEFLATED) as workbook:
        workbook.writestr("[Content_Types].xml", build_content_types_xml())
        workbook.writestr("_rels/.rels", build_root_rels_xml())
        workbook.writestr("docProps/app.xml", build_app_xml())
        workbook.writestr("docProps/core.xml", build_core_xml())
        workbook.writestr("xl/workbook.xml", build_workbook_xml())
        workbook.writestr("xl/_rels/workbook.xml.rels", build_workbook_rels_xml())
        workbook.writestr("xl/styles.xml", build_styles_xml())
        workbook.writestr("xl/worksheets/sheet1.xml", build_sheet_xml())


if __name__ == "__main__":
    write_workbook()
    print(OUTPUT)
