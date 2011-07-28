// iactivexView.cpp : implementation of the CiactivexView class
//

#include "stdafx.h"
#include "iactivex.h"

#include "iactivexDoc.h"
#include "iactivexView.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// CiactivexView

IMPLEMENT_DYNCREATE(CiactivexView, CView)

BEGIN_MESSAGE_MAP(CiactivexView, CView)
	// Standard printing commands
	ON_COMMAND(ID_FILE_PRINT, &CView::OnFilePrint)
	ON_COMMAND(ID_FILE_PRINT_DIRECT, &CView::OnFilePrint)
	ON_COMMAND(ID_FILE_PRINT_PREVIEW, &CView::OnFilePrintPreview)
END_MESSAGE_MAP()

// CiactivexView construction/destruction

CiactivexView::CiactivexView()
{
	// TODO: add construction code here

}

CiactivexView::~CiactivexView()
{
}

BOOL CiactivexView::PreCreateWindow(CREATESTRUCT& cs)
{
	// TODO: Modify the Window class or styles here by modifying
	//  the CREATESTRUCT cs

	return CView::PreCreateWindow(cs);
}

// CiactivexView drawing

void CiactivexView::OnDraw(CDC* /*pDC*/)
{
	CiactivexDoc* pDoc = GetDocument();
	ASSERT_VALID(pDoc);
	if (!pDoc)
		return;

	// TODO: add draw code for native data here
}


// CiactivexView printing

BOOL CiactivexView::OnPreparePrinting(CPrintInfo* pInfo)
{
	// default preparation
	return DoPreparePrinting(pInfo);
}

void CiactivexView::OnBeginPrinting(CDC* /*pDC*/, CPrintInfo* /*pInfo*/)
{
	// TODO: add extra initialization before printing
}

void CiactivexView::OnEndPrinting(CDC* /*pDC*/, CPrintInfo* /*pInfo*/)
{
	// TODO: add cleanup after printing
}


// CiactivexView diagnostics

#ifdef _DEBUG
void CiactivexView::AssertValid() const
{
	CView::AssertValid();
}

void CiactivexView::Dump(CDumpContext& dc) const
{
	CView::Dump(dc);
}

CiactivexDoc* CiactivexView::GetDocument() const // non-debug version is inline
{
	ASSERT(m_pDocument->IsKindOf(RUNTIME_CLASS(CiactivexDoc)));
	return (CiactivexDoc*)m_pDocument;
}
#endif //_DEBUG


// CiactivexView message handlers
