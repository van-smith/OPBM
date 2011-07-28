// iactivexView.h : interface of the CiactivexView class
//


#pragma once


class CiactivexView : public CView
{
protected: // create from serialization only
	CiactivexView();
	DECLARE_DYNCREATE(CiactivexView)

// Attributes
public:
	CiactivexDoc* GetDocument() const;

// Operations
public:

// Overrides
public:
	virtual void OnDraw(CDC* pDC);  // overridden to draw this view
	virtual BOOL PreCreateWindow(CREATESTRUCT& cs);
protected:
	virtual BOOL OnPreparePrinting(CPrintInfo* pInfo);
	virtual void OnBeginPrinting(CDC* pDC, CPrintInfo* pInfo);
	virtual void OnEndPrinting(CDC* pDC, CPrintInfo* pInfo);

// Implementation
public:
	virtual ~CiactivexView();
#ifdef _DEBUG
	virtual void AssertValid() const;
	virtual void Dump(CDumpContext& dc) const;
#endif

protected:

// Generated message map functions
protected:
	DECLARE_MESSAGE_MAP()
};

#ifndef _DEBUG  // debug version in iactivexView.cpp
inline CiactivexDoc* CiactivexView::GetDocument() const
   { return reinterpret_cast<CiactivexDoc*>(m_pDocument); }
#endif

