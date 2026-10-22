from __future__ import annotations

from datetime import UTC, datetime
from pathlib import Path
from xml.sax.saxutils import escape
import zipfile


ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "Authentication_Test_Results_Tested_So_Far.xlsx"

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
        "prerequisites": "Frontend on http://localhost:3000 and backend on http://localhost:8080 were running.",
        "steps": [
            "Opened the app.",
            "Switched to Customer Register.",
            "Entered a unique Gmail address and valid password.",
            "Submitted the form.",
        ],
        "expected": "Registration succeeds and returns user to customer login.",
        "actual": "Registration succeeded, success toast appeared, and the app returned to Customer sign in.",
        "status": "Pass",
    },
    {
        "id": "TC-02",
        "module": "1. User Authentication & Profile Management",
        "name": "Customer Registration with Duplicate Email",
        "objective": "Verify duplicate email registration is blocked.",
        "prerequisites": "A customer account had already been created with the same Gmail address.",
        "steps": [
            "Opened Customer Register.",
            "Entered the same Gmail address used in TC-01.",
            "Submitted the form.",
        ],
        "expected": "Registration is rejected with a duplicate-email error.",
        "actual": 'Registration was blocked and toast showed "Email already registered".',
        "status": "Pass",
    },
    {
        "id": "TC-03",
        "module": "1. User Authentication & Profile Management",
        "name": "Registration Email Validation",
        "objective": "Verify registration only accepts email addresses ending with @gmail.com.",
        "prerequisites": "Application was reachable in the browser.",
        "steps": [
            "Opened Customer Register.",
            "Entered a non-Gmail email address.",
            "Submitted the form.",
        ],
        "expected": "Registration is rejected with a clear @gmail.com validation message.",
        "actual": 'Registration was rejected, but the UI only showed the generic toast "Registration failed".',
        "status": "Fail",
    },
    {
        "id": "TC-04",
        "module": "1. User Authentication & Profile Management",
        "name": "Registration Password Validation",
        "objective": "Verify registration rejects passwords without alphabet, number, and special character.",
        "prerequisites": "Application was reachable in the browser.",
        "steps": [
            "Opened Customer Register.",
            "Entered a valid Gmail address and a weak password.",
            "Submitted the form.",
        ],
        "expected": "Registration is rejected with a clear password validation message.",
        "actual": 'Registration was rejected, but the UI only showed the generic toast "Registration failed".',
        "status": "Fail",
    },
    {
        "id": "TC-05",
        "module": "1. User Authentication & Profile Management",
        "name": "Customer Login (Happy Path)",
        "objective": "Verify a valid customer can log in successfully.",
        "prerequisites": "A CUSTOMER account existed from TC-01.",
        "steps": [
            "Opened Customer Login.",
            "Entered valid customer credentials.",
            "Clicked Login as Customer.",
        ],
        "expected": "Login succeeds and customer dashboard loads.",
        "actual": "Login succeeded and the customer dashboard loaded with products visible.",
        "status": "Pass",
    },
    {
        "id": "TC-06",
        "module": "1. User Authentication & Profile Management",
        "name": "Customer Login with Invalid Password",
        "objective": "Verify invalid credentials are rejected.",
        "prerequisites": "The test customer account existed.",
        "steps": [
            "Opened Customer Login.",
            "Entered the valid customer email with an incorrect password.",
            "Clicked Login as Customer.",
        ],
        "expected": "Login fails and user remains on login page.",
        "actual": 'Login was rejected and toast showed "Invalid credentials".',
        "status": "Pass",
    },
    {
        "id": "TC-07",
        "module": "1. User Authentication & Profile Management",
        "name": "Admin Login with Seeded Credentials",
        "objective": "Verify the seeded admin account can access the admin dashboard.",
        "prerequisites": "Backend was running with seeded admin credentials admin@shop.com / Admin@123.",
        "steps": [
            "Opened Admin Login.",
            "Entered seeded admin credentials.",
            "Clicked Login as Admin.",
        ],
        "expected": "Admin login succeeds and admin dashboard opens.",
        "actual": "Admin login succeeded and the Admin Console dashboard opened.",
        "status": "Pass",
    },
    {
        "id": "TC-08",
        "module": "1. User Authentication & Profile Management",
        "name": "Admin Account Attempted via Customer Login",
        "objective": "Verify admin credentials are blocked from the customer login flow.",
        "prerequisites": "Seeded admin account existed.",
        "steps": [
            "Opened Customer Login.",
            "Entered admin credentials.",
            "Clicked Login as Customer.",
        ],
        "expected": "Customer flow rejects admin account and prompts user to use admin option.",
        "actual": 'Login was blocked and toast showed "Please use the admin option for admin accounts."',
        "status": "Pass",
    },
    {
        "id": "TC-09",
        "module": "1. User Authentication & Profile Management",
        "name": "Forgot Password (Happy Path)",
        "objective": "Verify a customer can reset the password with a valid Gmail account.",
        "prerequisites": "A customer account existed with a Gmail address.",
        "steps": [
            "Opened Forgot password.",
            "Entered the existing customer Gmail address and a new valid password.",
            "Submitted Reset Password.",
            "Logged in with the new password.",
        ],
        "expected": "Password reset succeeds and login with the new password works.",
        "actual": "Password reset succeeded, returned to login, and login with the new password worked.",
        "status": "Pass",
    },
    {
        "id": "TC-11",
        "module": "1. User Authentication & Profile Management",
        "name": "Session Persistence After Browser Refresh",
        "objective": "Verify a logged-in session is restored after refreshing the page.",
        "prerequisites": "User was logged in as CUSTOMER.",
        "steps": [
            "Logged in successfully.",
            "Refreshed the browser tab.",
            "Observed the post-refresh state.",
        ],
        "expected": "User remains logged in and dashboard reloads without returning to login.",
        "actual": "Customer session remained active after refresh and dashboard reloaded normally.",
        "status": "Pass",
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
    title = "Authentication Manual Test Results (Tested So Far)"
    rows.append('<row r="1" ht="26" customHeight="1">' + inline_string_cell("A1", title, 2) + "</row>")

    header_cells = [
        inline_string_cell(f"{col_letter(idx)}2", header, 1) for idx, header in enumerate(HEADERS, start=1)
    ]
    rows.append('<row r="2" ht="24" customHeight="1">' + "".join(header_cells) + "</row>")

    for row_index, case in enumerate(TEST_CASES, start=3):
        values = [
            case["id"],
            case["module"],
            case["name"],
            case["objective"],
            case["prerequisites"],
            "\n".join(f"{idx}. {step}" for idx, step in enumerate(case["steps"], start=1)),
            case["expected"],
            case["actual"],
            case["status"],
        ]
        cells = [
            inline_string_cell(f"{col_letter(col_index)}{row_index}", value, 0)
            for col_index, value in enumerate(values, start=1)
        ]
        rows.append(f'<row r="{row_index}" ht="92" customHeight="1">{"".join(cells)}</row>')

    max_row = len(TEST_CASES) + 2
    cols = [
        (1, 1, 14),
        (2, 2, 28),
        (3, 3, 32),
        (4, 4, 34),
        (5, 5, 32),
        (6, 6, 42),
        (7, 7, 34),
        (8, 8, 36),
        (9, 9, 12),
    ]
    col_xml = "".join(
        f'<col min="{start}" max="{end}" width="{width}" customWidth="1"/>'
        for start, end, width in cols
    )

    return f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
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
  <mergeCells count="1"><mergeCell ref="A1:I1"/></mergeCells>
  <pageMargins left="0.7" right="0.7" top="0.75" bottom="0.75" header="0.3" footer="0.3"/>
</worksheet>"""


def build_styles_xml() -> str:
    return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="3">
    <font><sz val="11"/><name val="Calibri"/><family val="2"/></font>
    <font><b/><color rgb="FFFFFFFF"/><sz val="11"/><name val="Calibri"/><family val="2"/></font>
    <font><b/><color rgb="FFFFFFFF"/><sz val="14"/><name val="Calibri"/><family val="2"/></font>
  </fonts>
  <fills count="4">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FF0B5EA8"/><bgColor indexed="64"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FF2F7D32"/><bgColor indexed="64"/></patternFill></fill>
  </fills>
  <borders count="2">
    <border><left/><right/><top/><bottom/><diagonal/></border>
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
</styleSheet>"""


def build_workbook_xml() -> str:
    return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
          xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Auth Test Results" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>"""


def build_workbook_rels_xml() -> str:
    return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""


def build_root_rels_xml() -> str:
    return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
</Relationships>"""


def build_content_types_xml() -> str:
    return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>"""


def build_app_xml() -> str:
    return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
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
      <vt:lpstr>Auth Test Results</vt:lpstr>
    </vt:vector>
  </TitlesOfParts>
  <AppVersion>16.0300</AppVersion>
</Properties>"""


def build_core_xml() -> str:
    created = datetime.now(UTC).replace(microsecond=0).isoformat().replace("+00:00", "Z")
    return f"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
                   xmlns:dc="http://purl.org/dc/elements/1.1/"
                   xmlns:dcterms="http://purl.org/dc/terms/"
                   xmlns:dcmitype="http://purl.org/dc/dcmitype/"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dc:title>Authentication Test Results</dc:title>
  <dc:creator>Codex</dc:creator>
  <cp:lastModifiedBy>Codex</cp:lastModifiedBy>
  <dcterms:created xsi:type="dcterms:W3CDTF">{created}</dcterms:created>
  <dcterms:modified xsi:type="dcterms:W3CDTF">{created}</dcterms:modified>
</cp:coreProperties>"""


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
